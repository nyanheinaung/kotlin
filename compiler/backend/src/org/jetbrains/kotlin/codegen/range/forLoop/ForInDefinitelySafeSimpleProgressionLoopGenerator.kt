/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.forLoop

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.range.SimpleBoundedValue
import org.jetbrains.kotlin.codegen.range.comparison.ComparisonGenerator
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.org.objectweb.asm.Label

/**
 * Generates "naive" for loop:
 *
 * ```
 *  i = [| startValue |];
 *  [| if (!isStartInclusive) inc i |];
 *  end = [| endValue |];
 *  while ([| cmp(i, end) |]) ... {
 *      [| body |];
 *      inc i;
 *  }
 * ```
 *
 * This generator is suitable for cases where arithmetic overflow is impossible.
 */
class ForInDefinitelySafeSimpleProgressionLoopGenerator(
    codegen: ExpressionCodegen,
    forExpression: KtForExpression,
    private val startValue: StackValue,
    private val isStartInclusive: Boolean,
    private val endValue: StackValue,
    private val isEndInclusive: Boolean,
    comparisonGenerator: ComparisonGenerator,
    step: Int
) : AbstractForInRangeLoopGenerator(codegen, forExpression, step, comparisonGenerator) {

    constructor(
        codegen: ExpressionCodegen,
        forExpression: KtForExpression,
        boundedValue: SimpleBoundedValue,
        comparisonGenerator: ComparisonGenerator,
        step: Int
    ) : this(
        codegen, forExpression,
        startValue = if (step == 1) boundedValue.lowBound else boundedValue.highBound,
        isStartInclusive = if (step == 1) boundedValue.isLowInclusive else boundedValue.isHighInclusive,
        endValue = if (step == 1) boundedValue.highBound else boundedValue.lowBound,
        isEndInclusive = if (step == 1) boundedValue.isHighInclusive else boundedValue.isLowInclusive,
        comparisonGenerator = comparisonGenerator,
        step = step
    )

    companion object {
        fun fromBoundedValueWithStep1(
            codegen: ExpressionCodegen,
            forExpression: KtForExpression,
            boundedValue: SimpleBoundedValue,
            comparisonGenerator: ComparisonGenerator
        ) =
            ForInDefinitelySafeSimpleProgressionLoopGenerator(codegen, forExpression, boundedValue, comparisonGenerator, 1)

        fun fromBoundedValueWithStepMinus1(
            codegen: ExpressionCodegen,
            forExpression: KtForExpression,
            boundedValue: SimpleBoundedValue,
            comparisonGenerator: ComparisonGenerator
        ) =
            ForInDefinitelySafeSimpleProgressionLoopGenerator(codegen, forExpression, boundedValue, comparisonGenerator, -1)
    }

    override fun storeRangeStartAndEnd() {
        loopParameter().store(startValue, v)
        // Skip 1st element if start is not inclusive.
        if (!isStartInclusive) incrementLoopVariable()

        StackValue.local(endVar, asmElementType).store(endValue, v)
    }

    override fun checkEmptyLoop(loopExit: Label) {
    }

    override fun checkPreCondition(loopExit: Label) {
        loopParameter().put(asmElementType, elementType, v)
        v.load(endVar, asmElementType)

        if (step > 0) {
            if (isEndInclusive)
                comparisonGenerator.jumpIfGreater(v, loopExit)
            else
                comparisonGenerator.jumpIfGreaterOrEqual(v, loopExit)
        } else {
            if (isEndInclusive)
                comparisonGenerator.jumpIfLess(v, loopExit)
            else
                comparisonGenerator.jumpIfLessOrEqual(v, loopExit)
        }
    }

    override fun checkPostConditionAndIncrement(loopExit: Label) {
        incrementLoopVariable()
    }
}