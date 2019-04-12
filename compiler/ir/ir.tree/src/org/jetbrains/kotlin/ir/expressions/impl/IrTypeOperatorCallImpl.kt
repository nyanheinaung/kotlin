/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrTypeOperator
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.symbols.IrClassifierSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrTypeOperatorCallImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val operator: IrTypeOperator,
    override val typeOperand: IrType
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrTypeOperatorCall {

    override lateinit var argument: IrExpression
    override lateinit var typeOperandClassifier: IrClassifierSymbol

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        operator: IrTypeOperator,
        typeOperand: IrType,
        typeOperandClassifier: IrClassifierSymbol,
        argument: IrExpression
    ) : this(startOffset, endOffset, type, operator, typeOperand) {
        this.argument = argument
        this.typeOperandClassifier = typeOperandClassifier
    }

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitTypeOperator(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        argument.accept(visitor, data)
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        argument = argument.transform(transformer, data)
    }
}