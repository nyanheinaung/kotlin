/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.range.forLoop

import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Type

abstract class AbstractForInProgressionOrRangeLoopGenerator(codegen: ExpressionCodegen, forExpression: KtForExpression) :
    AbstractForLoopGenerator(codegen, forExpression) {
    protected var endVar: Int = -1

    private var loopParameter: StackValue? = null

    init {
        assert(
            asmElementType.sort == Type.INT ||
                    asmElementType.sort == Type.BYTE ||
                    asmElementType.sort == Type.SHORT ||
                    asmElementType.sort == Type.CHAR ||
                    asmElementType.sort == Type.LONG
        ) {
            "Unexpected range element type: " + asmElementType
        }
    }

    override fun beforeLoop() {
        super.beforeLoop()

        endVar = createLoopTempVariable(asmElementType)
    }

    protected fun checkPostCondition(loopExit: Label) {
        assert(endVar != -1) {
            "endVar must be allocated, endVar = " + endVar
        }
        loopParameter().put(asmElementType, elementType, v)
        v.load(endVar, asmElementType)
        if (asmElementType.sort == Type.LONG) {
            v.lcmp()
            v.ifeq(loopExit)
        } else {
            v.ificmpeq(loopExit)
        }
    }

    override fun checkPreCondition(loopExit: Label) {}

    protected fun loopParameter(): StackValue =
        loopParameter ?: StackValue.local(loopParameterVar, loopParameterType).also { loopParameter = it }
}
