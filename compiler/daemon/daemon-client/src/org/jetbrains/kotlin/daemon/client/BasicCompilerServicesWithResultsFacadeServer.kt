/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.client

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.OutputMessageUtil
import org.jetbrains.kotlin.daemon.common.*
import java.io.File
import java.io.Serializable
import java.rmi.server.UnicastRemoteObject


open class BasicCompilerServicesWithResultsFacadeServer(
        val messageCollector: MessageCollector,
        val outputsCollector: ((File, List<File>) -> Unit)? = null,
        port: Int = SOCKET_ANY_FREE_PORT
) : CompilerServicesFacadeBase,
    UnicastRemoteObject(port, LoopbackNetworkInterface.clientLoopbackSocketFactory, LoopbackNetworkInterface.serverLoopbackSocketFactory)
{
    override fun report(category: Int, severity: Int, message: String?, attachment: Serializable?) {
        messageCollector.reportFromDaemon(outputsCollector, category, severity, message, attachment)
    }
}

fun MessageCollector.reportFromDaemon(outputsCollector: ((File, List<File>) -> Unit)?, category: Int, severity: Int, message: String?, attachment: Serializable?) {
    val reportCategory = ReportCategory.fromCode(category)

    when (reportCategory) {
        ReportCategory.OUTPUT_MESSAGE -> {
            if (outputsCollector != null) {
                OutputMessageUtil.parseOutputMessage(message.orEmpty())?.let { outs ->
                    outs.outputFile?.let {
                        outputsCollector.invoke(it, outs.sourceFiles.toList())
                    }
                }
            }
            else {
                report(CompilerMessageSeverity.OUTPUT, message!!)
            }
        }
        ReportCategory.EXCEPTION -> {
            report(CompilerMessageSeverity.EXCEPTION, message.orEmpty())
        }
        ReportCategory.COMPILER_MESSAGE -> {
            val compilerSeverity = when (ReportSeverity.fromCode(severity)) {
                ReportSeverity.ERROR -> CompilerMessageSeverity.ERROR
                ReportSeverity.WARNING -> CompilerMessageSeverity.WARNING
                ReportSeverity.INFO -> CompilerMessageSeverity.INFO
                ReportSeverity.DEBUG -> CompilerMessageSeverity.LOGGING
                else -> throw IllegalStateException("Unexpected compiler message report severity $severity")
            }
            if (message != null) {
                report(compilerSeverity, message, attachment as? CompilerMessageLocation)
            }
            else {
                reportUnexpected(category, severity, message, attachment)
            }
        }
        ReportCategory.DAEMON_MESSAGE,
        ReportCategory.IC_MESSAGE -> {
            if (message != null) {
                report(CompilerMessageSeverity.LOGGING, message)
            }
            else {
                reportUnexpected(category, severity, message, attachment)
            }
        }
        else -> {
            reportUnexpected(category, severity, message, attachment)
        }
    }
}

private fun MessageCollector.reportUnexpected(category: Int, severity: Int, message: String?, attachment: Serializable?) {
    val compilerMessageSeverity = when (ReportSeverity.fromCode(severity)) {
        ReportSeverity.ERROR -> CompilerMessageSeverity.ERROR
        ReportSeverity.WARNING -> CompilerMessageSeverity.WARNING
        ReportSeverity.INFO -> CompilerMessageSeverity.INFO
        else -> CompilerMessageSeverity.LOGGING
    }

    report(compilerMessageSeverity, "Unexpected message: category=$category; severity=$severity; message='$message'; attachment=$attachment")
}
