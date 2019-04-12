/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.builtins.functions

import org.jetbrains.kotlin.generators.builtins.generateBuiltIns.BuiltInsSourceGenerator
import java.io.PrintWriter

val MAX_PARAM_COUNT = 22

class GenerateFunctions(out: PrintWriter) : BuiltInsSourceGenerator(out) {
    override fun getPackage() = "kotlin.jvm.functions"

    fun generateTypeParameters(i: Int, variance: Boolean) {
        out.print("<")

        for (j in 1..i) {
            if (variance) out.print("in ")
            out.print("P$j, ")
        }

        generateReturnTypeParameter(variance)

        out.print(">")
    }

    fun generateReturnTypeParameter(variance: Boolean) {
        if (variance) out.print("out ")
        out.print("R")
    }

    override fun generateBody() {
        for (i in 0..MAX_PARAM_COUNT) {
            generateDocumentation(i)
            out.print("public interface Function$i")
            generateTypeParameters(i, variance = true)
            generateSuperClass()
            generateFunctionClassBody(i)
        }
    }

    fun generateDocumentation(i: Int) {
        val suffix = if (i == 1) "" else "s"
        out.println("/** A function that takes $i argument${suffix}. */")
    }

    fun generateSuperClass() {
        out.print(" : Function<")
        generateReturnTypeParameter(variance = false)
        out.print(">")
    }

    fun generateFunctionClassBody(i: Int) {
        out.println(" {")
        generateInvokeSignature(i)
        out.println("}")
    }

    fun generateInvokeSignature(i: Int) {
        if (i == 0) {
            out.println("    /** Invokes the function. */")
        }
        else {
            val suffix = if (i == 1) "" else "s"
            out.println("    /** Invokes the function with the specified argument${suffix}. */")
        }
        out.print("    public operator fun invoke(")
        for (j in 1..i) {
            out.print("p$j: P$j")
            if (j < i) {
                out.print(", ")
            }
        }
        out.println("): R")
    }
}
