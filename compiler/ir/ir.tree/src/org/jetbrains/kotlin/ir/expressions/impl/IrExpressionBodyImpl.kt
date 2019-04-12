/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.IrElementBase
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrExpressionBodyImpl(
    startOffset: Int,
    endOffset: Int
) :
    IrElementBase(startOffset, endOffset),
    IrExpressionBody {

    constructor(startOffset: Int, endOffset: Int, expression: IrExpression) : this(startOffset, endOffset) {
        this.expression = expression
    }

    constructor(expression: IrExpression) : this(expression.startOffset, expression.endOffset, expression)

    override lateinit var expression: IrExpression

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitExpressionBody(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        expression.accept(visitor, data)
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        expression = expression.transform(transformer, data)
    }
}