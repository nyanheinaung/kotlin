/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrSetVariable
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrVariableSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrSetVariableImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val symbol: IrVariableSymbol,
    override val origin: IrStatementOrigin?
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrSetVariable {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrVariableSymbol,
        value: IrExpression,
        origin: IrStatementOrigin?
    ) : this(startOffset, endOffset, type, symbol, origin) {
        this.value = value
    }

    override val descriptor: VariableDescriptor get() = symbol.descriptor

    override lateinit var value: IrExpression

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitSetVariable(this, data)
    }

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        value.accept(visitor, data)
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        value = value.transform(transformer, data)
    }
}
