/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.forLoop

import org.jetbrains.kotlin.codegen.AsmUtil.genIncrement
import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.range.comparison.ComparisonGenerator
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type

abstract class AbstractForInRangeLoopGenerator(
    codegen: ExpressionCodegen,
    forExpression: KtForExpression,
    protected val step: Int,
    protected val comparisonGenerator: ComparisonGenerator
) : AbstractForInProgressionOrRangeLoopGenerator(codegen, forExpression) {

    override fun beforeLoop() {
        super.beforeLoop()

        storeRangeStartAndEnd()
    }

    protected abstract fun storeRangeStartAndEnd()

    override fun checkEmptyLoop(loopExit: Label) {
        loopParameter().put(asmElementType, elementType, v)
        v.load(endVar, asmElementType)

        if (step > 0) {
            comparisonGenerator.jumpIfGreater(v, loopExit)
        } else {
            comparisonGenerator.jumpIfLess(v, loopExit)
        }
    }

    override fun assignToLoopParameter() {}

    override fun checkPostConditionAndIncrement(loopExit: Label) {
        checkPostCondition(loopExit)

        incrementLoopVariable()
    }

    protected fun incrementLoopVariable() {
        if (loopParameterType === Type.INT_TYPE) {
            v.iinc(loopParameterVar, step)
        } else {
            val loopParameter = loopParameter()
            loopParameter.put(asmElementType, elementType, v)
            genIncrement(asmElementType, step, v)
            loopParameter.store(StackValue.onStack(asmElementType, elementType), v)
        }
    }

    init {
        assert(step == 1 || step == -1) { "'step' should be either 1 or -1: " + step }
    }
}
