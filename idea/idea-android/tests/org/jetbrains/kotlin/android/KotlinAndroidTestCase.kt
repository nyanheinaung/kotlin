/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import com.android.SdkConstants
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File


abstract class KotlinAndroidTestCase : AndroidTestCase() {
    override fun getTestDataPath() = KotlinTestUtils.getHomeDirectory()

    override fun createManifest() {
        myFixture.copyFileToProject("idea/testData/android/AndroidManifest.xml", SdkConstants.FN_ANDROID_MANIFEST_XML)
    }

    fun copyResourceDirectoryForTest(path: String) {
        val testFile = File(path)
        if (testFile.isFile) {
            myFixture.copyDirectoryToProject(testFile.parent + "/res", "res")
        } else if (testFile.isDirectory) {
            myFixture.copyDirectoryToProject(testFile.path + "/res", "res")
        }
    }
}