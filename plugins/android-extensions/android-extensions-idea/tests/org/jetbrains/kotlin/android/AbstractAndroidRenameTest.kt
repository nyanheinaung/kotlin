/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.PlatformTestUtil

abstract class AbstractAndroidRenameTest : KotlinAndroidTestCase() {
    private val NEW_NAME = "NEWNAME"
    private val NEW_ID_NAME = "@+id/$NEW_NAME"

    fun doTest(path: String) {
        copyResourceDirectoryForTest(path)
        val virtualFile = myFixture.copyFileToProject("$path${getTestName(true)}.kt", "src/${getTestName(true)}.kt")
        myFixture.configureFromExistingVirtualFile(virtualFile)
        myFixture.renameElement(myFixture.elementAtCaret, NEW_ID_NAME)
        myFixture.checkResultByFile("$path/expected/${getTestName(true)}.kt")
        assertResourcesEqual("$path/expected/res")
    }

    fun assertResourcesEqual(expectedPath: String) {
        PlatformTestUtil.assertDirectoriesEqual(LocalFileSystem.getInstance().findFileByPath(expectedPath), getResourceDirectory())
    }

    fun getResourceDirectory() = LocalFileSystem.getInstance().findFileByPath(myFixture.tempDirPath + "/res")
}
