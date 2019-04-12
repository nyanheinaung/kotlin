/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.builtins.arrayIterators

import org.jetbrains.kotlin.generators.builtins.PrimitiveType
import org.jetbrains.kotlin.generators.builtins.generateBuiltIns.*
import java.io.PrintWriter

class GenerateArrayIterators(out: PrintWriter) : BuiltInsSourceGenerator(out) {
    override fun getPackage() = "kotlin.jvm.internal"

    override fun generateBody() {
        for (kind in PrimitiveType.values()) {
            val s = kind.capitalized
            out.println("private class Array${s}Iterator(private val array: ${s}Array) : ${s}Iterator() {")
            out.println("    private var index = 0")
            out.println("    override fun hasNext() = index < array.size")
            out.println("    override fun next$s() = try { array[index++] } catch (e: ArrayIndexOutOfBoundsException) { index -= 1; throw NoSuchElementException(e.message) }")
            out.println("}")
            out.println()
        }
        for (kind in PrimitiveType.values()) {
            val s = kind.capitalized
            out.println("public fun iterator(array: ${s}Array): ${s}Iterator = Array${s}Iterator(array)")
        }
    }
}
