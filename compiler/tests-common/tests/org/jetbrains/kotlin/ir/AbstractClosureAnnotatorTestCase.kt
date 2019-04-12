/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir

import org.jetbrains.kotlin.backend.common.AbstractClosureAnnotator
import org.jetbrains.kotlin.backend.common.Closure
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ConstructorDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter

abstract class AbstractClosureAnnotatorTestCase : AbstractIrGeneratorTestCase() {
    override fun doTest(wholeFile: File, testFiles: List<TestFile>) {
        val dir = wholeFile.parentFile
        val ignoreErrors = shouldIgnoreErrors(wholeFile)
        for ((testFile, irFile) in generateIrFilesAsSingleModule(testFiles, ignoreErrors)) {
            doTestIrFileAgainstExpectations(dir, testFile, irFile)
        }
    }

    private fun doTestIrFileAgainstExpectations(dir: File, testFile: TestFile, irFile: IrFile) {
        val expectedFile = File(dir, testFile.name.replace(".kt", ".closure"))
        val actualClosures = renderClosures(irFile)
        KotlinTestUtils.assertEqualsToFile(expectedFile, actualClosures)
    }

    private fun renderClosures(irFile: IrFile): String {
        val actualStringWriter = StringWriter()
        val actualOut = PrintWriter(actualStringWriter)

        irFile.acceptChildrenVoid(object : AbstractClosureAnnotator() {
            override fun recordClassClosure(classDescriptor: ClassDescriptor, closure: Closure) {
                actualOut.println("Closure for class ${classDescriptor.name}:")
                printClosure(closure)
                actualOut.println()
            }

            override fun recordFunctionClosure(functionDescriptor: FunctionDescriptor, closure: Closure) {
                if (functionDescriptor is ConstructorDescriptor) {
                    actualOut.println("Closure for constructor ${functionDescriptor.containingDeclaration.name}:")
                } else {
                    actualOut.println("Closure for function ${functionDescriptor.name}:")
                }
                printClosure(closure)
                actualOut.println()
            }

            private fun printClosure(closure: Closure) {
                closure.capturedValues.forEach {
                    actualOut.println("  variable ${it.name}")
                }
            }
        })

        return actualStringWriter.toString()
    }
}
