/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.checkers

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.impl.LocalVariableDescriptor
import org.jetbrains.kotlin.descriptors.impl.SyntheticFieldDescriptor
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.checkers.AdditionalTypeChecker
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.jvm.diagnostics.ErrorsJvm
import org.jetbrains.kotlin.types.KotlinType

object JvmArrayVariableInLoopAssignmentChecker : AdditionalTypeChecker {

    override fun checkType(
            expression: KtExpression,
            expressionType: KotlinType,
            expressionTypeWithSmartCast: KotlinType,
            c: ResolutionContext<*>
    ) {
        if (c.languageVersionSettings.supportsFeature(LanguageFeature.ProperForInArrayLoopRangeVariableAssignmentSemantic)) return

        val binaryExpression = expression as? KtBinaryExpression ?: return
        if (binaryExpression.operationToken != KtTokens.EQ) return

        val lhsExpression = binaryExpression.left as? KtSimpleNameExpression ?: return
        val resolvedCall = lhsExpression.getResolvedCall(c.trace.bindingContext) ?: return
        val variableDescriptor = resolvedCall.resultingDescriptor as? LocalVariableDescriptor ?: return
        if (variableDescriptor is SyntheticFieldDescriptor) return
        if (variableDescriptor.isDelegated) return

        val variableType = variableDescriptor.returnType
        if (!KotlinBuiltIns.isArrayOrPrimitiveArray(variableType)) return

        if (!isOuterForLoopRangeVariable(expression, variableDescriptor, c)) return

        val dataFlowValueKind = c.dataFlowValueFactory.createDataFlowValue(lhsExpression, variableType, c).kind
        if (dataFlowValueKind != DataFlowValue.Kind.STABLE_VARIABLE) return

        c.trace.report(ErrorsJvm.ASSIGNMENT_TO_ARRAY_LOOP_VARIABLE.on(lhsExpression))
    }

    private fun isOuterForLoopRangeVariable(
            expression: KtExpression,
            variableDescriptor: CallableDescriptor,
            c: ResolutionContext<*>
    ): Boolean {
        for (parent in expression.parents) {
            if (parent is KtForExpression) {
                val rangeExpression = parent.loopRange as? KtSimpleNameExpression ?: continue
                val rangeResolvedCall = rangeExpression.getResolvedCall(c.trace.bindingContext) ?: continue
                if (rangeResolvedCall.resultingDescriptor == variableDescriptor) {
                    return true
                }
            }
        }
        return false
    }
}