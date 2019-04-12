/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers.javac

import org.jetbrains.kotlin.checkers.AbstractDiagnosticsTestWithStdLib
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.test.InTextDirectivesUtils
import org.jetbrains.kotlin.test.KotlinTestUtils.getHomeDirectory
import java.io.File

abstract class AbstractDiagnosticsTestWithStdLibUsingJavac : AbstractDiagnosticsTestWithStdLib() {

    override fun analyzeAndCheck(testDataFile: File, files: List<TestFile>) {
        val testDataFileText = testDataFile.readText()
        if (InTextDirectivesUtils.isDirectiveDefined(testDataFileText, "// JAVAC_SKIP")) {
            println("${testDataFile.name} test is skipped")
            return
        }
        val groupedByModule = files.groupBy(TestFile::module)
        val allKtFiles = groupedByModule.values.flatMap { getKtFiles(it, true) }

        if (InTextDirectivesUtils.isDirectiveDefined(testDataFileText, "// FULL_JDK")) {
            environment.registerJavac(kotlinFiles = allKtFiles)
        }
        else {
            val mockJdk = listOf(File(getHomeDirectory(), "compiler/testData/mockJDK/jre/lib/rt.jar"))
            environment.registerJavac(kotlinFiles = allKtFiles, bootClasspath = mockJdk)
        }

        environment.configuration.put(JVMConfigurationKeys.USE_JAVAC, true)
        super.analyzeAndCheck(testDataFile, files)
    }
}