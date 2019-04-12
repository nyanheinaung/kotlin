/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.builtins.iterators

import org.jetbrains.kotlin.generators.builtins.PrimitiveType
import org.jetbrains.kotlin.generators.builtins.generateBuiltIns.*
import java.io.PrintWriter

class GenerateIterators(out: PrintWriter) : BuiltInsSourceGenerator(out) {
    override fun getPackage() = "kotlin.collections"
    override fun generateBody() {
        for (kind in PrimitiveType.values()) {
            val s = kind.capitalized
            out.println("/** An iterator over a sequence of values of type `$s`. */")
            out.println("public abstract class ${s}Iterator : Iterator<$s> {"   )
            out.println("    override final fun next() = next$s()")
            out.println()
            out.println("    /** Returns the next value in the sequence without boxing. */")
            out.println("    public abstract fun next$s(): $s")
            out.println("}")
            out.println()
        }
    }
}
