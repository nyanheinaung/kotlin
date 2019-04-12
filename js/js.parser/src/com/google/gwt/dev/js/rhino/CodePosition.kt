/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.google.gwt.dev.js.rhino

class CodePosition(val line: Int, val offset: Int) : Comparable<CodePosition> {
    override fun compareTo(other: CodePosition): Int =
            when {
                line < other.line -> -1
                line > other.line -> 1
                else -> offset.compareTo(other.offset)
            }

    override fun toString(): String = "($line, $offset)"
}
