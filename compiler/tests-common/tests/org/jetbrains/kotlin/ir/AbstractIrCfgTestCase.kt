/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir

import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.path
import org.jetbrains.kotlin.ir2cfg.generators.FunctionGenerator
import org.jetbrains.kotlin.ir2cfg.util.dump
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File

abstract class AbstractIrCfgTestCase : AbstractIrGeneratorTestCase() {

    private fun IrFile.cfgDump(): String {
        val builder = StringBuilder()
        for (declaration in this.declarations) {
            if (declaration is IrFunction) {
                builder.appendln("// FUN: ${declaration.name}")
                val cfg = FunctionGenerator(declaration).generate()
                builder.appendln(cfg.dump())
                builder.appendln("// END FUN: ${declaration.name}")
            }
        }
        return builder.toString()
    }

    private fun IrModuleFragment.cfgDump(): String {
        val builder = StringBuilder()
        for (file in this.files) {
            builder.appendln("// FILE: ${file.path}")
            builder.appendln(file.cfgDump())
            builder.appendln("// END FILE: ${file.path}")
            builder.appendln()
        }
        return builder.toString()
    }

    override fun doTest(wholeFile: File, testFiles: List<TestFile>) {
        val irModule = generateIrModule(false)
        val irModuleDump = irModule.cfgDump()
        val expectedPath = wholeFile.canonicalPath.replace(".kt", ".txt")
        val expectedFile = File(expectedPath)
        KotlinTestUtils.assertEqualsToFile(expectedFile, irModuleDump)
    }
}