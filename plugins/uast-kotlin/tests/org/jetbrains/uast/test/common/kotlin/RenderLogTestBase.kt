/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.test.common.kotlin

import org.jetbrains.uast.UFile
import org.jetbrains.uast.asRecursiveLogString
import org.jetbrains.uast.test.env.kotlin.assertEqualsToFile
import java.io.File

interface RenderLogTestBase {
    fun getTestFile(testName: String, ext: String): File

    private fun getRenderFile(testName: String) = getTestFile(testName, "render.txt")
    private fun getLogFile(testName: String) = getTestFile(testName, "log.txt")

    fun check(testName: String, file: UFile) {
        val renderFile = getRenderFile(testName)
        val logFile = getLogFile(testName)

        assertEqualsToFile("Render string", renderFile, file.asRenderString())
        assertEqualsToFile("Log string", logFile, file.asRecursiveLogString())
    }
}