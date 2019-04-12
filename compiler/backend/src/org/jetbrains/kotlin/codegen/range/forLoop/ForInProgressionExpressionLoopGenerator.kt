/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.forLoop

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression

class ForInProgressionExpressionLoopGenerator(
    codegen: ExpressionCodegen,
    forExpression: KtForExpression,
    private val rangeExpression: KtExpression
) : AbstractForInProgressionLoopGenerator(codegen, forExpression) {

    override fun storeProgressionParametersToLocalVars() {
        codegen.gen(rangeExpression, asmLoopRangeType, rangeKotlinType)
        v.dup()
        v.dup()

        generateRangeOrProgressionProperty(asmLoopRangeType, "getFirst", asmElementType, loopParameterType, loopParameterVar)
        generateRangeOrProgressionProperty(asmLoopRangeType, "getLast", asmElementType, asmElementType, endVar)
        generateRangeOrProgressionProperty(asmLoopRangeType, "getStep", incrementType, incrementType, incrementVar)
    }
}
