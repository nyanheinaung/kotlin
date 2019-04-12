/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.messages

interface MessageCollector {
    fun clear()

    fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation? = null)

    fun hasErrors(): Boolean

    companion object {
        val NONE: MessageCollector = object : MessageCollector {
            override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
                // Do nothing
            }

            override fun clear() {
                // Do nothing
            }

            override fun hasErrors(): Boolean = false
        }
    }
}
