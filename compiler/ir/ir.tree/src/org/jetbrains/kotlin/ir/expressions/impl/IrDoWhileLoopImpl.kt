/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrDoWhileLoop
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrDoWhileLoopImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    origin: IrStatementOrigin?
) :
    IrLoopBase(startOffset, endOffset, type, origin),
    IrDoWhileLoop {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        origin: IrStatementOrigin?,
        body: IrExpression,
        condition: IrExpression
    ) : this(startOffset, endOffset, type, origin) {
        this.condition = condition
        this.body = body
    }

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitDoWhileLoop(this, data)
    }

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        body?.accept(visitor, data)
        condition.accept(visitor, data)
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        body = body?.transform(transformer, data)
        condition = condition.transform(transformer, data)
    }
}