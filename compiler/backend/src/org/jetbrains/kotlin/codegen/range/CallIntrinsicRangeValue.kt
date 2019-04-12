/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.range.inExpression.CallBasedInExpressionGenerator
import org.jetbrains.kotlin.codegen.range.inExpression.InExpressionGenerator
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCallWithAssert
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

abstract class CallIntrinsicRangeValue(protected val rangeCall: ResolvedCall<out CallableDescriptor>) : RangeValue {
    protected abstract fun isIntrinsicInCall(resolvedCallForIn: ResolvedCall<out CallableDescriptor>): Boolean

    protected abstract fun createIntrinsicInExpressionGenerator(
        codegen: ExpressionCodegen,
        operatorReference: KtSimpleNameExpression,
        resolvedCall: ResolvedCall<out CallableDescriptor>
    ): InExpressionGenerator

    override fun createInExpressionGenerator(codegen: ExpressionCodegen, operatorReference: KtSimpleNameExpression): InExpressionGenerator {
        val resolvedCall = operatorReference.getResolvedCallWithAssert(codegen.bindingContext)
        return if (isIntrinsicInCall(resolvedCall))
            createIntrinsicInExpressionGenerator(codegen, operatorReference, resolvedCall)
        else
            CallBasedInExpressionGenerator(codegen, operatorReference)
    }
}