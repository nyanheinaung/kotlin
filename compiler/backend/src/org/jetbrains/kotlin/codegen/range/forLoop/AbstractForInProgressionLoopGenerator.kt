/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.forLoop

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.range.comparison.SignedIntegerComparisonGenerator
import org.jetbrains.kotlin.codegen.range.comparison.getComparisonGeneratorForKotlinType
import org.jetbrains.kotlin.codegen.range.getRangeOrProgressionElementType
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type

abstract class AbstractForInProgressionLoopGenerator(
    codegen: ExpressionCodegen,
    forExpression: KtForExpression
) : AbstractForInProgressionOrRangeLoopGenerator(codegen, forExpression) {

    protected var incrementVar: Int = -1
    protected val asmLoopRangeType: Type
    protected val rangeKotlinType = bindingContext.getType(forExpression.loopRange!!)!!
    private val rangeElementKotlinType = getRangeOrProgressionElementType(rangeKotlinType)
        ?: throw AssertionError("Unexpected loop range type: $rangeKotlinType")
    private val incrementKotlinType: KotlinType
    protected val incrementType: Type

    init {
        asmLoopRangeType = codegen.asmType(rangeKotlinType)

        val incrementProp = rangeKotlinType.memberScope.getContributedVariables(Name.identifier("step"), NoLookupLocation.FROM_BACKEND)
        assert(incrementProp.size == 1) { rangeKotlinType.toString() + " " + incrementProp.size }
        incrementKotlinType = incrementProp.single().type
        incrementType = codegen.asmType(incrementKotlinType)
    }

    private val incrementComparisonGenerator =
        getComparisonGeneratorForKotlinType(incrementKotlinType) as? SignedIntegerComparisonGenerator
            ?: throw AssertionError("Unexpected increment type: $incrementKotlinType")

    private val elementComparisonGenerator = getComparisonGeneratorForKotlinType(rangeElementKotlinType)

    override fun beforeLoop() {
        super.beforeLoop()

        incrementVar = createLoopTempVariable(asmElementType)

        storeProgressionParametersToLocalVars()
    }

    protected abstract fun storeProgressionParametersToLocalVars()

    override fun checkEmptyLoop(loopExit: Label) {
        loopParameter().put(asmElementType, elementType, v)
        v.load(endVar, asmElementType)
        v.load(incrementVar, incrementType)

        val negativeIncrement = Label()
        val afterIf = Label()

        incrementComparisonGenerator.jumpIfLessThanZero(v, negativeIncrement)
        elementComparisonGenerator.jumpIfGreater(v, loopExit)
        v.goTo(afterIf)
        v.mark(negativeIncrement)
        elementComparisonGenerator.jumpIfLess(v, loopExit)
        v.mark(afterIf)
    }

    override fun assignToLoopParameter() {}

    override fun checkPostConditionAndIncrement(loopExit: Label) {
        checkPostCondition(loopExit)

        val loopParameter = loopParameter()
        loopParameter.put(asmElementType, elementType, v)
        v.load(incrementVar, asmElementType)
        v.add(asmElementType)

        if (asmElementType === Type.BYTE_TYPE || asmElementType === Type.SHORT_TYPE || asmElementType === Type.CHAR_TYPE) {
            StackValue.coerce(Type.INT_TYPE, asmElementType, v)
        }

        loopParameter.store(StackValue.onStack(asmElementType, elementType), v)
    }
}