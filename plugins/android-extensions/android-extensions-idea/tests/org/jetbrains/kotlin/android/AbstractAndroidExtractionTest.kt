/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import org.jetbrains.kotlin.idea.refactoring.introduce.ExtractTestFiles
import org.jetbrains.kotlin.idea.refactoring.introduce.checkExtract
import org.jetbrains.kotlin.idea.refactoring.introduce.doExtractFunction
import org.jetbrains.kotlin.psi.KtFile

abstract class AbstractAndroidExtractionTest: KotlinAndroidTestCase() {
    fun doTest(path: String) {
        copyResourceDirectoryForTest(path)
        val testFilePath = path + getTestName(true) + ".kt"
        val virtualFile = myFixture.copyFileToProject(testFilePath, "src/" + getTestName(true) + ".kt")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        checkExtract(ExtractTestFiles(testFilePath, myFixture.file)) { file ->
            doExtractFunction(myFixture, file as KtFile)
        }
    }
}