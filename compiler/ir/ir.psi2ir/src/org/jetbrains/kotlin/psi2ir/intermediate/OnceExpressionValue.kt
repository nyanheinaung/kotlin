/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.render
import org.jetbrains.kotlin.types.KotlinType

abstract class ExpressionValue(override val type: IrType) : IntermediateValue

inline fun generateExpressionValue(type: IrType, crossinline generate: () -> IrExpression) =
    object : ExpressionValue(type) {
        override fun load(): IrExpression = generate()
    }

inline fun generateDelegatedValue(type: IrType, crossinline generateValue: () -> IntermediateValue) =
    object : ExpressionValue(type) {
        val lazyDelegate by lazy { generateValue() }
        override fun load(): IrExpression = lazyDelegate.load()
    }

class OnceExpressionValue(val irExpression: IrExpression) : LValue, AssignmentReceiver {
    private var instantiated = false

    override fun load(): IrExpression {
        if (instantiated) throw AssertionError("Single expression value for ${irExpression.render()} is already instantiated")
        instantiated = true
        return irExpression
    }

    override val type: IrType get() = irExpression.type

    override fun store(irExpression: IrExpression): IrExpression {
        throw AssertionError("Expression value ${irExpression.render()} can't be used in store operation")
    }

    override fun assign(withLValue: (LValue) -> IrExpression): IrExpression =
        withLValue(this)
}