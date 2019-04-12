/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.inline.util

import org.jetbrains.kotlin.js.backend.ast.*
import org.jetbrains.kotlin.js.backend.ast.metadata.SideEffectKind
import org.jetbrains.kotlin.js.backend.ast.metadata.sideEffects
import org.jetbrains.kotlin.js.translate.utils.jsAstUtils.any

fun JsExpression.canHaveSideEffect(localVars: Set<JsName>) =
        any { it is JsExpression && it.canHaveOwnSideEffect(localVars) }

fun JsExpression.canHaveOwnSideEffect(vars: Set<JsName>) = when (this) {
    is JsConditional,
    is JsLiteral -> false
    is JsBinaryOperation -> operator.isAssignment
    is JsNameRef -> name !in vars && sideEffects != SideEffectKind.PURE
    else -> sideEffects != SideEffectKind.PURE
}
