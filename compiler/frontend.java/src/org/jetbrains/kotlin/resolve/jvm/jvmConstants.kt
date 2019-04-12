/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.load.java.descriptors.JavaPropertyDescriptor
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.KtVisitorVoid
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.constants.evaluate.ConstantExpressionEvaluator

fun getCompileTimeConstant(
        expression: KtExpression,
        bindingContext: BindingContext,
        takeUpConstValsAsConst: Boolean,
        shouldInlineConstVals: Boolean
): ConstantValue<*>? {
    val compileTimeValue = ConstantExpressionEvaluator.getConstant(expression, bindingContext)
    if (compileTimeValue == null || compileTimeValue.usesNonConstValAsConstant) {
        return null
    }

    if (!shouldInlineConstVals && !takeUpConstValsAsConst && compileTimeValue.usesVariableAsConstant) {
        val constantChecker = ConstantsChecker(bindingContext)
        expression.accept(constantChecker)
        if (constantChecker.containsKotlinConstVals) return null
    }

    val expectedType = bindingContext.getType(expression) ?: return null

    return compileTimeValue.toConstantValue(expectedType)
}


private class ConstantsChecker(private val bindingContext: BindingContext) : KtVisitorVoid() {
    var containsKotlinConstVals = false

    override fun visitSimpleNameExpression(expression: KtSimpleNameExpression) {
        val resolvedCall = expression.getResolvedCall(bindingContext)
        if (resolvedCall != null) {
            val callableDescriptor = resolvedCall.resultingDescriptor
            if (callableDescriptor is PropertyDescriptor && callableDescriptor !is JavaPropertyDescriptor && callableDescriptor.isConst) {
                containsKotlinConstVals = true
            }
        }
    }

    override fun visitKtElement(element: KtElement) {
        if (!containsKotlinConstVals) {
            element.acceptChildren(this)
        }
    }
}
