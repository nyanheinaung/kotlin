/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android


abstract class AbstractAndroidFindUsagesTest : KotlinAndroidTestCase() {

    fun doTest(path: String) {
        return // TODO: investigate and fix this test
        copyResourceDirectoryForTest(path)
        val testFileName = getTestName(true) + ".kt"
        val virtualFile = myFixture.copyFileToProject(path + testFileName, "src/" + getTestName(true) + ".kt")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        val propUsages = myFixture.findUsages(myFixture.elementAtCaret)
        assertTrue(propUsages.isNotEmpty())
    }
}