/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.builtins.progressionIterators

import org.jetbrains.kotlin.generators.builtins.*
import org.jetbrains.kotlin.generators.builtins.generateBuiltIns.*
import org.jetbrains.kotlin.generators.builtins.ProgressionKind.*
import java.io.PrintWriter

fun integerProgressionIterator(kind: ProgressionKind): String {
    val t = kind.capitalized

    val incrementType = progressionIncrementType(kind)

    val (toInt, toType) = when (kind) {
        CHAR -> ".toInt()" to ".to$t()"
        else -> "" to ""
    }

    return """/**
 * An iterator over a progression of values of type `$t`.
 * @property step the number by which the value is incremented on each step.
 */
internal class ${t}ProgressionIterator(first: $t, last: $t, val step: $incrementType) : ${t}Iterator() {
    private val finalElement = last$toInt
    private var hasNext: Boolean = if (step > 0) first <= last else first >= last
    private var next = if (hasNext) first$toInt else finalElement

    override fun hasNext(): Boolean = hasNext

    override fun next$t(): $t {
        val value = next
        if (value == finalElement) {
            if (!hasNext) throw kotlin.NoSuchElementException()
            hasNext = false
        }
        else {
            next += step
        }
        return value$toType
    }
}"""
}


class GenerateProgressionIterators(out: PrintWriter) : BuiltInsSourceGenerator(out) {
    override fun getPackage() = "kotlin.ranges"
    override fun generateBody() {
        for (kind in ProgressionKind.values()) {
            out.println(integerProgressionIterator(kind))
            out.println()
        }
    }
}
