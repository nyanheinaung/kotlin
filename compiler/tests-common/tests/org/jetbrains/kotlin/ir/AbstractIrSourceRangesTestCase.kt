/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir

import org.jetbrains.kotlin.ir.util.RenderIrElementVisitor
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.test.KotlinTestUtils
import org.jetbrains.kotlin.utils.Printer
import java.io.File

abstract class AbstractIrSourceRangesTestCase : AbstractIrGeneratorTestCase() {
    override fun doTest(wholeFile: File, testFiles: List<TestFile>) {
        val dir = wholeFile.parentFile
        val testFileToIrFile = generateIrFilesAsSingleModule(testFiles)
        for ((testFile, irFile) in testFileToIrFile) {
            val irFileDump = irFile.dumpWithSourceLocations(irFile.fileEntry)
            val expectedSourceLocations = File(dir, testFile.name.replace(".kt", ".txt"))
            KotlinTestUtils.assertEqualsToFile(expectedSourceLocations, irFileDump)
        }
    }

    private fun IrElement.dumpWithSourceLocations(fileEntry: SourceManager.FileEntry): String =
        StringBuilder().also {
            acceptVoid(DumpSourceLocations(it, fileEntry))
        }.toString()

    private class DumpSourceLocations(
        out: Appendable,
        val fileEntry: SourceManager.FileEntry
    ) : IrElementVisitorVoid {
        val printer = Printer(out, "  ")
        val elementRenderer = RenderIrElementVisitor()

        override fun visitElement(element: IrElement) {
            val sourceRangeInfo = fileEntry.getSourceRangeInfo(element.startOffset, element.endOffset)
            printer.println("@${sourceRangeInfo.render()} ${element.accept(elementRenderer, null)}")
            printer.pushIndent()
            element.acceptChildrenVoid(this)
            printer.popIndent()
        }

        private fun SourceRangeInfo.render() =
            if (startLineNumber == endLineNumber)
                "$startLineNumber:$startColumnNumber..$endColumnNumber"
            else
                "$startLineNumber:$startColumnNumber..$endLineNumber:$endColumnNumber"
    }
}
