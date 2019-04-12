/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.test.kotlin

import org.jetbrains.uast.test.common.kotlin.TypesTestBase
import java.io.File

abstract class AbstractKotlinTypesTest : AbstractKotlinUastTest(), TypesTestBase {

    private fun getTestFile(testName: String, ext: String) =
            File(File(TEST_KOTLIN_MODEL_DIR, testName).canonicalPath + '.' + ext)

    override fun getTypesFile(testName: String) = getTestFile(testName, "types.txt")
}