/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.semantics

import org.jetbrains.kotlin.js.test.BasicBoxTest
import java.io.File

abstract class AbstractOutputPrefixPostfixTest : BasicBoxTest(
        BasicBoxTest.TEST_DATA_DIR_PATH + "outputPrefixPostfix/",
        "outputPrefixPostfix/",
        generateNodeJsRunner = false
) {
    override fun getOutputPrefixFile(testFilePath: String): File? {
        return newFileIfExists(testFilePath + ".prefix")
    }

    override fun getOutputPostfixFile(testFilePath: String): File? {
        return newFileIfExists(testFilePath + ".postfix")
    }

    override fun performAdditionalChecks(generatedJsFiles: List<String>, outputPrefixFile: File?, outputPostfixFile: File?) {
        super.performAdditionalChecks(generatedJsFiles, outputPrefixFile, outputPostfixFile)

        val output = File(generatedJsFiles.first()).readText()

        outputPrefixFile?.let {
            assertTrue(output.startsWith(it.readText()))
        }
        outputPostfixFile?.let {
            assertTrue(output.endsWith(it.readText()))
        }
    }

    private fun newFileIfExists(path: String): File? {
        val file = File(path)
        if (!file.exists()) return null
        return file
    }
}