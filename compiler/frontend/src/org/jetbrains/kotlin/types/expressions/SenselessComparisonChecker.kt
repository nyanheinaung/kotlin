/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions

import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.calls.smartcasts.Nullability
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isError

object SenselessComparisonChecker {
    @JvmStatic
    fun checkSenselessComparisonWithNull(
        expression: KtBinaryExpression,
        left: KtExpression,
        right: KtExpression,
        context: ResolutionContext<*>,
        getType: (KtExpression) -> KotlinType?,
        getNullability: (DataFlowValue) -> Nullability
    ) {
        val expr =
            when {
                KtPsiUtil.isNullConstant(left) -> right
                KtPsiUtil.isNullConstant(right) -> left
                else -> return
            }

        val type = getType(expr)
        if (type == null || type.isError) return

        val operationSign = expression.operationReference
        val value = context.dataFlowValueFactory.createDataFlowValue(expr, type, context)

        val equality =
            operationSign.getReferencedNameElementType() == KtTokens.EQEQ || operationSign.getReferencedNameElementType() == KtTokens.EQEQEQ
        val nullability = getNullability(value)

        val expressionIsAlways =
            when (nullability) {
                Nullability.NULL -> equality
                Nullability.NOT_NULL -> !equality
                Nullability.IMPOSSIBLE -> false
                else -> return
            }

        context.trace.report(Errors.SENSELESS_COMPARISON.on(expression, expression, expressionIsAlways))
    }
}
