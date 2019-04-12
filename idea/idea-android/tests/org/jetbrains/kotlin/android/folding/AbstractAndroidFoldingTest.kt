/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.folding

import org.jetbrains.kotlin.android.KotlinAndroidTestCase
import java.io.File


abstract class AbstractAndroidResourceFoldingTest : KotlinAndroidTestCase() {

    fun doTest(path: String) {
        val testFile = File(path)
        myFixture.copyFileToProject("${testFile.parent}/values.xml", "res/values/values.xml")
        myFixture.copyFileToProject("${testFile.parent}/R.java", "gen/com/myapp/R.java")
        myFixture.testFoldingWithCollapseStatus(path, "${myFixture.tempDirPath}/src/main.kt")
    }
}