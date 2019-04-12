/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

interface IrWhen : IrExpression {
    val origin: IrStatementOrigin?

    val branches: MutableList<IrBranch>
}

interface IrBranch : IrElement {
    var condition: IrExpression
    var result: IrExpression

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrBranch =
        transformer.visitBranch(this, data)

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitBranch(this, data)
}

interface IrElseBranch : IrBranch {
    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrElseBranch =
        transformer.visitElseBranch(this, data)

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitElseBranch(this, data)
}

