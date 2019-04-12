/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import org.jetbrains.kotlin.idea.test.ConfigLibraryUtil
import java.io.File

abstract class AbstractParcelCheckerTest : KotlinAndroidTestCase() {
    override fun setUp() {
        super.setUp()
        ConfigLibraryUtil.configureKotlinRuntime(myModule)
    }

    override fun tearDown() {
        ConfigLibraryUtil.unConfigureKotlinRuntime(myModule)
        super.tearDown()
    }

    fun doTest(filename: String) {
        myFixture.copyDirectoryToProject("plugins/android-extensions/android-extensions-runtime/src", "src/androidExtensionsRuntime")

        val ktFile = File(filename)
        val virtualFile = myFixture.copyFileToProject(ktFile.absolutePath, "src/" + getTestName(true) + ".kt")
        myFixture.configureFromExistingVirtualFile(virtualFile)

        myFixture.checkHighlighting(true, false, true)
    }
}