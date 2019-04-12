/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.range.comparison.getComparisonGeneratorForKotlinType
import org.jetbrains.kotlin.codegen.range.forLoop.ForInRangeInstanceLoopGenerator
import org.jetbrains.kotlin.codegen.range.forLoop.ForLoopGenerator
import org.jetbrains.kotlin.codegen.range.inExpression.CallBasedInExpressionGenerator
import org.jetbrains.kotlin.codegen.range.inExpression.InExpressionGenerator
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression
import org.jetbrains.kotlin.types.KotlinType

class PrimitiveRangeRangeValue(private val rangeExpression: KtExpression) : ReversableRangeValue {

    override fun createForLoopGenerator(codegen: ExpressionCodegen, forExpression: KtForExpression) =
        ForInRangeInstanceLoopGenerator(
            codegen, forExpression, rangeExpression,
            getComparisonGeneratorForKotlinType(getRangeElementType(codegen, forExpression)),
            reversed = false
        )

    override fun createInExpressionGenerator(codegen: ExpressionCodegen, operatorReference: KtSimpleNameExpression): InExpressionGenerator =
        CallBasedInExpressionGenerator(codegen, operatorReference)

    override fun createForInReversedLoopGenerator(codegen: ExpressionCodegen, forExpression: KtForExpression): ForLoopGenerator =
        ForInRangeInstanceLoopGenerator(
            codegen, forExpression, rangeExpression,
            getComparisonGeneratorForKotlinType(getRangeElementType(codegen, forExpression)),
            reversed = true
        )

    private fun getRangeElementType(codegen: ExpressionCodegen, forExpression: KtForExpression): KotlinType {
        val ktLoopRange = forExpression.loopRange
            ?: throw AssertionError("No loop range expression: ${forExpression.text}")
        val rangeType = codegen.bindingContext.getType(ktLoopRange)
            ?: throw AssertionError("No type for loop range expression: ${ktLoopRange.text}")
        return getRangeOrProgressionElementType(rangeType)
            ?: throw AssertionError("Unexpected range type: $rangeType")
    }
}