/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.test.kotlin

import org.jetbrains.uast.test.common.kotlin.ValuesTestBase
import java.io.File

abstract class AbstractKotlinValuesTest : AbstractKotlinUastTest(), ValuesTestBase {

    private fun getTestFile(testName: String, ext: String) =
            File(File(AbstractKotlinUastTest.TEST_KOTLIN_MODEL_DIR, testName).canonicalPath + '.' + ext)

    override fun getValuesFile(testName: String) = getTestFile(testName, "values.txt")
}