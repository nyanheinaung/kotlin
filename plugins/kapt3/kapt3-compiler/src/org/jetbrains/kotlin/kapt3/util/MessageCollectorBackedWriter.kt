/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt3.util

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.GroupingMessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import java.io.Writer

class MessageCollectorBackedWriter(
    private val messageCollector: MessageCollector,
    private val severity: CompilerMessageSeverity
) : Writer() {
    override fun write(buffer: CharArray, offset: Int, length: Int) {
        val message = String(buffer, offset, length).trim().trim('\n', '\r')
        if (message.isNotEmpty()) {
            messageCollector.report(severity, message)
        }
    }

    override fun flush() {
        if (messageCollector is GroupingMessageCollector) {
            messageCollector.flush()
        }
    }

    override fun close() {
        flush()
    }
}
