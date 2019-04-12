/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jvm.compiler

import org.jetbrains.kotlin.test.KotlinTestUtils
import java.io.File

abstract class AbstractLoadJavaWithFastClassReadingTest : AbstractLoadJavaTest() {
    override fun useFastClassFilesReading() = true

    override fun getExpectedFile(expectedFileName: String): File {
        val differentResultFile = KotlinTestUtils.replaceExtension(File(expectedFileName), "fast.txt")
        if (differentResultFile.exists()) return differentResultFile
        return super.getExpectedFile(expectedFileName)
    }
}
