/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.generateCallReceiver
import org.jetbrains.kotlin.codegen.generateCallSingleArgument
import org.jetbrains.kotlin.codegen.range.forLoop.IteratorForLoopGenerator
import org.jetbrains.kotlin.codegen.range.inExpression.InContinuousRangeOfComparableExpressionGenerator
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class ComparableRangeLiteralRangeValue(
    codegen: ExpressionCodegen,
    rangeCall: ResolvedCall<out CallableDescriptor>
) : CallIntrinsicRangeValue(rangeCall) {
    private val boundedValue = SimpleBoundedValue(
        instanceType = codegen.asmType(rangeCall.resultingDescriptor.returnType!!),
        lowBound = codegen.generateCallReceiver(rangeCall),
        highBound = codegen.generateCallSingleArgument(rangeCall)
    )

    override fun createForLoopGenerator(codegen: ExpressionCodegen, forExpression: KtForExpression) =
        IteratorForLoopGenerator(codegen, forExpression)

    override fun isIntrinsicInCall(resolvedCallForIn: ResolvedCall<out CallableDescriptor>) =
        isClosedRangeContains(resolvedCallForIn.resultingDescriptor)

    override fun createIntrinsicInExpressionGenerator(
        codegen: ExpressionCodegen,
        operatorReference: KtSimpleNameExpression,
        resolvedCall: ResolvedCall<out CallableDescriptor>
    ) = InContinuousRangeOfComparableExpressionGenerator(operatorReference, boundedValue, codegen.frameMap)
}