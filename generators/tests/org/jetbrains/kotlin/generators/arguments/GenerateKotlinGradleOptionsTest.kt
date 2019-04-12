/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.arguments.test

import com.intellij.testFramework.UsefulTestCase
import org.jetbrains.kotlin.generators.arguments.generateKotlinGradleOptions
import org.jetbrains.kotlin.utils.Printer
import java.io.*

class GenerateKotlinGradleOptionsTest : UsefulTestCase() {
    fun testKotlinGradleOptionsAreUpToDate() {
        fun getPrinter(file: File, fn: Printer.()->Unit) {
            val bytesOut = ByteArrayOutputStream()

            PrintStream(bytesOut).use {
                val printer = Printer(it)
                printer.fn()
            }

            val upToDateContent = bytesOut.toString()
            UsefulTestCase.assertSameLinesWithFile(file.absolutePath, upToDateContent)
        }

        generateKotlinGradleOptions(::getPrinter)
    }
}
