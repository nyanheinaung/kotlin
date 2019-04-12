/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.scratch

import org.jetbrains.kotlin.idea.scratch.output.ScratchOutput
import org.jetbrains.kotlin.idea.scratch.output.ScratchOutputHandler

abstract class ScratchExecutor(protected val file: ScratchFile) {
    abstract fun execute()
    abstract fun stop()

    protected val handler = CompositeOutputHandler()

    fun addOutputHandler(outputHandler: ScratchOutputHandler) {
        handler.add(outputHandler)
    }

    fun errorOccurs(message: String, e: Throwable? = null, isFatal: Boolean = false) {
        handler.error(file, message)

        if (isFatal) {
            handler.onFinish(file)
        }

        if (e != null) LOG.error(e)
    }

    protected class CompositeOutputHandler : ScratchOutputHandler {
        private val handlers = mutableListOf<ScratchOutputHandler>()

        fun add(handler: ScratchOutputHandler) {
            handlers.add(handler)
        }

        override fun onStart(file: ScratchFile) {
            handlers.forEach { it.onStart(file) }
        }

        override fun handle(file: ScratchFile, expression: ScratchExpression, output: ScratchOutput) {
            handlers.forEach { it.handle(file, expression, output) }
        }

        override fun error(file: ScratchFile, message: String) {
            handlers.forEach { it.error(file, message) }
        }

        override fun onFinish(file: ScratchFile) {
            handlers.forEach { it.onFinish(file) }
        }

        override fun clear(file: ScratchFile) {
            handlers.forEach { it.clear(file) }
        }
    }
}