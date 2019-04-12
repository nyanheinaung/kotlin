/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.compilerRunner

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.OutputMessageUtil

class MessageCollectorToOutputItemsCollectorAdapter(
        private val delegate: MessageCollector,
        private val outputCollector: OutputItemsCollector
) : MessageCollector by delegate {
    override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
        // TODO: consider adding some other way of passing input -> output mapping from compiler, e.g. dedicated service
        OutputMessageUtil.parseOutputMessage(message)?.let {
            outputCollector.add(it.sourceFiles, it.outputFile)
        }
        delegate.report(severity, message, location)
    }
}