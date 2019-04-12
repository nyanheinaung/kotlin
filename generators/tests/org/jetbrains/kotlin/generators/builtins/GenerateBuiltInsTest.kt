/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.generators.builtins.test

import java.io.PrintWriter
import org.jetbrains.kotlin.generators.builtins.generateBuiltIns.generateBuiltIns
import java.io.StringWriter
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.util.io.FileUtil
import org.junit.Assert
import com.intellij.testFramework.UsefulTestCase

class GenerateBuiltInsTest : UsefulTestCase() {
    fun testBuiltInsAreUpToDate() {
        generateBuiltIns { file, generator ->
            val sw = StringWriter()
            PrintWriter(sw).use {
                generator(it).generate()
            }

            val expected = StringUtil.convertLineSeparators(sw.toString().trim())
            val actual = StringUtil.convertLineSeparators(FileUtil.loadFile(file).trim())

            Assert.assertEquals("To fix this problem you need to regenerate built-ins (run generateBuiltIns.kt)", expected, actual)
        }
    }
}
