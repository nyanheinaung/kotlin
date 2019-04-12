/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.checkers.javac

import org.jetbrains.kotlin.checkers.AbstractForeignAnnotationsTest
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import java.io.File

abstract class AbstractJavacForeignAnnotationsTest : AbstractForeignAnnotationsTest() {

    override fun analyzeAndCheck(testDataFile: File, files: List<TestFile>) {
        val groupedByModule = files.groupBy(TestFile::module)
        val allKtFiles = groupedByModule.values.flatMap { getKtFiles(it, true) }
        environment.registerJavac(kotlinFiles = allKtFiles)
        environment.configuration.put(JVMConfigurationKeys.USE_JAVAC, true)

        super.analyzeAndCheck(testDataFile, files)
    }

}