/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.test.utils

import org.jetbrains.kotlin.js.backend.ast.JsExpressionStatement
import org.jetbrains.kotlin.js.backend.ast.JsNullLiteral
import org.jetbrains.kotlin.js.backend.ast.JsProgram
import org.jetbrains.kotlin.js.backend.ast.RecursiveJsVisitor
import org.junit.Assert

fun JsProgram.verifyAst() {
    accept(object : RecursiveJsVisitor() {
        override fun visitExpressionStatement(x: JsExpressionStatement) {
            val expression = x.expression
            if (expression is JsNullLiteral) {
                Assert.fail("Expression statement contains `null` literal")
            }
            else {
                super.visitExpressionStatement(x)
            }
        }
    })
}