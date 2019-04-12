/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.forLoop

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.range.comparison.ComparisonGenerator
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtForExpression

class ForInRangeInstanceLoopGenerator(
    codegen: ExpressionCodegen,
    forExpression: KtForExpression,
    private val rangeExpression: KtExpression,
    comparisonGenerator: ComparisonGenerator,
    private val reversed: Boolean
) : AbstractForInRangeLoopGenerator(codegen, forExpression, if (reversed) -1 else 1, comparisonGenerator) {

    override fun storeRangeStartAndEnd() {
        val loopRangeType = codegen.bindingContext.getType(rangeExpression)!!
        val asmLoopRangeType = codegen.asmType(loopRangeType)
        codegen.gen(rangeExpression, asmLoopRangeType, loopRangeType)
        v.dup()

        // ranges inherit first and last from corresponding progressions
        if (reversed) {
            generateRangeOrProgressionProperty(asmLoopRangeType, "getLast", asmElementType, loopParameterType, loopParameterVar)
            generateRangeOrProgressionProperty(asmLoopRangeType, "getFirst", asmElementType, asmElementType, endVar)
        } else {
            generateRangeOrProgressionProperty(asmLoopRangeType, "getFirst", asmElementType, loopParameterType, loopParameterVar)
            generateRangeOrProgressionProperty(asmLoopRangeType, "getLast", asmElementType, asmElementType, endVar)
        }
    }
}
