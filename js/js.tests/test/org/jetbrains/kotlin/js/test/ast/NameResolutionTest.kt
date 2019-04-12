/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.ast

import com.google.gwt.dev.js.rhino.CodePosition
import com.google.gwt.dev.js.rhino.ErrorReporter
import com.intellij.openapi.util.io.FileUtil
import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.inline.clean.renameLabels
import org.jetbrains.kotlin.js.inline.clean.resolveTemporaryNames
import org.jetbrains.kotlin.js.parser.parse
import org.jetbrains.kotlin.js.test.BasicBoxTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.io.File

class NameResolutionTest {
    @Rule
    @JvmField
    var testName = TestName()

    @Test
    fun simple() = doTest()

    @Test
    fun reuseName() = doTest()

    @Test
    fun globalName() = doTest()

    @Test
    fun labels() = doTest()

    private fun doTest() {
        val methodName = testName.methodName
        val baseName = "${BasicBoxTest.TEST_DATA_DIR_PATH}/js-name-resolution/"
        val originalName = "$baseName/$methodName.original.js"
        val expectedName = "$baseName/$methodName.expected.js"

        val originalCode = FileUtil.loadFile(File(originalName))
        val expectedCode = FileUtil.loadFile(File(expectedName))

        val parserScope = JsFunctionScope(JsRootScope(JsProgram()), "<js fun>")
        val originalAst = JsGlobalBlock().apply { statements += parse(originalCode, errorReporter, parserScope, originalName).orEmpty() }
        val expectedAst = JsGlobalBlock().apply { statements += parse(expectedCode, errorReporter, parserScope, expectedName).orEmpty() }

        originalAst.accept(object : RecursiveJsVisitor() {
            val cache = mutableMapOf<JsName, JsName>()

            override fun visitElement(node: JsNode) {
                super.visitElement(node)
                if (node is HasName) {
                    node.name = node.name?.let { name ->
                        if (name.ident.startsWith("$")) {
                            cache.getOrPut(name) { JsScope.declareTemporaryName("x") }
                        } else {
                            name
                        }
                    }
                }
            }
        })
        renameLabels(originalAst)
        originalAst.resolveTemporaryNames()

        assertEquals(expectedAst.toString(), originalAst.toString())
    }

    private val errorReporter = object : ErrorReporter {
        override fun warning(message: String, startPosition: CodePosition, endPosition: CodePosition) {}

        override fun error(message: String, startPosition: CodePosition, endPosition: CodePosition) {
            fail("Error parsing JS file: $message at $startPosition")
        }
    }
}
