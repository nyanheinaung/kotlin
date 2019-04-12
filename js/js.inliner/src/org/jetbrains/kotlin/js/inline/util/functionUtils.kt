/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util

import org.jetbrains.kotlin.js.backend.ast.JsFunction
import org.jetbrains.kotlin.js.backend.ast.JsReturn

fun isFunctionCreator(outer: JsFunction): Boolean =
        outer.getInnerFunction() != null

/**
 * Gets inner function from function, that creates closure
 *
 * For example:
 * function(a) {
 *   return function() { return a; }
 * }
 *
 * Inner functions can only be generated when lambda
 * with closure is created
 */
fun JsFunction.getInnerFunction(): JsFunction? {
    val statements = body.statements
    if (statements.size != 1) return null

    val statement = statements.get(0)
    val returnExpr = (statement as? JsReturn)?.expression

    return returnExpr as? JsFunction
}
