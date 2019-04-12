/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers.javac

import org.jetbrains.kotlin.checkers.AbstractDiagnosticsTest
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File

abstract class AbstractJavacDiagnosticsTest : AbstractDiagnosticsTest() {

    private var useJavac = true

    override fun analyzeAndCheck(testDataFile: File, files: List<TestFile>) {
        if (useJavac) {
            val groupedByModule = files.groupBy(TestFile::module)
            val allKtFiles = groupedByModule.values.flatMap { getKtFiles(it, true) }
            environment.registerJavac(kotlinFiles = allKtFiles)
            environment.configuration.put(JVMConfigurationKeys.USE_JAVAC, true)
        }
        super.analyzeAndCheck(testDataFile, files)
    }

    fun doTestWithoutJavacWrapper(path: String) {
        useJavac = false
        super.doTest(path)
    }

    override fun getExpectedDiagnosticsFile(testDataFile: File): File {
        val suffix = if (useJavac) ".WithJavac.txt" else ".WithoutJavac.txt"
        val specialFile = File(testDataFile.parent, testDataFile.name + suffix)
        return specialFile.takeIf { it.exists() } ?: super.getExpectedDiagnosticsFile(testDataFile)
    }

    override fun createTestFiles(file: File, expectedText: String, modules: MutableMap<String, ModuleAndDependencies>?): List<TestFile> {
        val specialFile = getExpectedDiagnosticsFile(file)
        if (file.path == specialFile.path) {
            return super.createTestFiles(file, expectedText, modules)
        }

        return super.createTestFiles(specialFile, KotlinTestUtils.doLoadFile(specialFile), modules)
    }
}

