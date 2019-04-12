/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.range.forLoop.IteratorForLoopGenerator
import org.jetbrains.kotlin.codegen.range.inExpression.CallBasedInExpressionGenerator
import org.jetbrains.kotlin.codegen.range.inExpression.InExpressionGenerator
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtSimpleNameExpression

class IterableRangeValue : RangeValue {
    override fun createForLoopGenerator(codegen: ExpressionCodegen, forExpression: KtForExpression) =
        IteratorForLoopGenerator(codegen, forExpression)

    override fun createInExpressionGenerator(codegen: ExpressionCodegen, operatorReference: KtSimpleNameExpression): InExpressionGenerator =
        CallBasedInExpressionGenerator(codegen, operatorReference)
}