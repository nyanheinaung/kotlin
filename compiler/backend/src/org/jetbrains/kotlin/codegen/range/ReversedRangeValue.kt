/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.range.forLoop.ForLoopGenerator
import org.jetbrains.kotlin.codegen.range.inExpression.InExpressionGenerator
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

class ReversedRangeValue(private val original: ReversableRangeValue) : RangeValue, ReversableRangeValue {
    override fun createInExpressionGenerator(codegen: ExpressionCodegen, operatorReference: KtSimpleNameExpression): InExpressionGenerator =
        original.createInExpressionGenerator(codegen, operatorReference)

    override fun createForLoopGenerator(codegen: ExpressionCodegen, forExpression: KtForExpression): ForLoopGenerator =
        original.createForInReversedLoopGenerator(codegen, forExpression)

    override fun createForInReversedLoopGenerator(codegen: ExpressionCodegen, forExpression: KtForExpression): ForLoopGenerator =
        original.createForLoopGenerator(codegen, forExpression)
}