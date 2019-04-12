/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android

import org.jetbrains.kotlin.android.quickfix.AbstractAndroidQuickFixMultiFileTest
import org.jetbrains.kotlin.idea.test.ConfigLibraryUtil
import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File

abstract class AbstractParcelQuickFixTest : AbstractAndroidQuickFixMultiFileTest() {
    override fun setUp() {
        super.setUp()

        val androidSdk = KotlinTestUtils.findAndroidSdk()
        val androidJarDir = File(androidSdk, "platforms").listFiles().first { it.name.startsWith("android-") }
        ConfigLibraryUtil.addLibrary(myModule, "androidJar", androidJarDir.absolutePath, arrayOf("android.jar"))

        ConfigLibraryUtil.addLibrary(myModule, "androidExtensionsRuntime", "dist/kotlinc/lib", arrayOf("android-extensions-runtime.jar"))
    }

    override fun tearDown() {
        ConfigLibraryUtil.removeLibrary(myModule, "androidJar")
        ConfigLibraryUtil.removeLibrary(myModule, "androidExtensionsRuntime")

        super.tearDown()
    }
}