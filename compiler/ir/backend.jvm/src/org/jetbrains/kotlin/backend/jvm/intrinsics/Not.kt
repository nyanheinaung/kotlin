/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.intrinsics

import org.jetbrains.kotlin.backend.jvm.codegen.BlockInfo
import org.jetbrains.kotlin.backend.jvm.codegen.BooleanValue
import org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen
import org.jetbrains.kotlin.backend.jvm.codegen.coerceToBoolean
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.org.objectweb.asm.Label

class Not : IntrinsicMethod() {
    class BooleanNegation(val value: BooleanValue) : BooleanValue(value.mv) {
        override fun jumpIfFalse(target: Label) = value.jumpIfTrue(target)
        override fun jumpIfTrue(target: Label) = value.jumpIfFalse(target)
    }

    override fun invoke(expression: IrFunctionAccessExpression, codegen: ExpressionCodegen, data: BlockInfo) =
        BooleanNegation(expression.dispatchReceiver!!.accept(codegen, data).coerceToBoolean())
}
