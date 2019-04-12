/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.utils.repl

enum class ReplEscapeType {
    INITIAL_PROMPT,
    HELP_PROMPT,
    USER_OUTPUT,
    REPL_RESULT,
    READLINE_START,
    READLINE_END,
    REPL_INCOMPLETE,
    COMPILE_ERROR,
    RUNTIME_ERROR,
    INTERNAL_ERROR,
    SUCCESS;

    companion object {
        fun valueOfOrNull(string: String): ReplEscapeType? {
            return try {
                valueOf(string)
            }
            catch (e: IllegalArgumentException) {
                null
            }
        }
    }
}