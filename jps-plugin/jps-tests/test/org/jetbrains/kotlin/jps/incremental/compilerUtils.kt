/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.incremental

import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JSCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.js.K2JSCompiler
import org.jetbrains.kotlin.compilerRunner.*
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.jps.build.KotlinBuilder
import org.jetbrains.kotlin.utils.PathUtil
import java.io.*

fun createTestingCompilerEnvironment(
    messageCollector: MessageCollector,
    outputItemsCollector: OutputItemsCollectorImpl,
    services: Services
): JpsCompilerEnvironment {
    val paths = PathUtil.kotlinPathsForDistDirectory

    val wrappedMessageCollector = MessageCollectorToOutputItemsCollectorAdapter(messageCollector, outputItemsCollector)
    return JpsCompilerEnvironment(
        paths,
        services,
        KotlinBuilder.classesToLoadByParent,
        wrappedMessageCollector,
        outputItemsCollector,
        MockProgressReporter
    )
}

fun runJSCompiler(args: K2JSCompilerArguments, env: JpsCompilerEnvironment): ExitCode? {
    val argsArray = ArgumentUtils.convertArgumentsToStringList(args).toTypedArray()

    val stream = ByteArrayOutputStream()
    val out = PrintStream(stream)
    val exitCode = CompilerRunnerUtil.invokeExecMethod(K2JSCompiler::class.java.name, argsArray, env, out)
    val reader = BufferedReader(StringReader(stream.toString()))
    CompilerOutputParser.parseCompilerMessagesFromReader(env.messageCollector, reader, env.outputItemsCollector)
    return exitCode as? ExitCode
}

private object MockProgressReporter : ProgressReporter {
    override fun progress(message: String) {
    }

    override fun compilationStarted() {
    }

    override fun clearProgress() {
    }
}