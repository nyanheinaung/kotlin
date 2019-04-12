/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.parser

import com.google.gwt.dev.js.rhino.CodePosition

class OffsetToSourceMapping(text: String) {
    private val data: IntArray

    init {
        var i = 0
        val lineSeparators = mutableListOf<Int>()
        lineSeparators += 0
        while (i < text.length) {
            val c = text[i++]
            val isNewLine = when (c) {
                '\r' -> {
                    if (i < text.length && text[i] == '\n') {
                        ++i
                    }
                    true
                }
                '\n' -> true
                else -> false
            }
            if (isNewLine) {
                lineSeparators += i
            }
        }

        data = lineSeparators.toIntArray()
    }

    operator fun get(offset: Int): CodePosition {
        val lineNumber = data.binarySearch(offset).let { if (it >= 0) it else -it - 2 }
        return CodePosition(lineNumber, offset - data[lineNumber])
    }
}