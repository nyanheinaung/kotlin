/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.IrElementBase
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import java.util.*

abstract class IrWhenBase(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val origin: IrStatementOrigin? = null
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrWhen {

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitWhen(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        branches.forEach { it.accept(visitor, data) }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        branches.forEachIndexed { i, irBranch ->
            branches[i] = irBranch.transform(transformer, data)
        }
    }
}

class IrWhenImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val origin: IrStatementOrigin? = null
) :
    IrWhenBase(startOffset, endOffset, type) {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        origin: IrStatementOrigin?,
        branches: List<IrBranch>
    ) : this(startOffset, endOffset, type, origin) {
        this.branches.addAll(branches)
    }

    override val branches: MutableList<IrBranch> = ArrayList()
}

open class IrBranchImpl(
    startOffset: Int,
    endOffset: Int,
    override var condition: IrExpression,
    override var result: IrExpression
) :
    IrElementBase(startOffset, endOffset),
    IrBranch {

    constructor(condition: IrExpression, result: IrExpression) :
            this(condition.startOffset, condition.endOffset, condition, result)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        condition.accept(visitor, data)
        result.accept(visitor, data)
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        condition = condition.transform(transformer, data)
        result = result.transform(transformer, data)
    }

}

class IrElseBranchImpl(
    startOffset: Int,
    endOffset: Int,
    condition: IrExpression,
    result: IrExpression
) :
    IrBranchImpl(startOffset, endOffset, condition, result),
    IrElseBranch {

    constructor(condition: IrExpression, result: IrExpression) : this(condition.startOffset, condition.endOffset, condition, result)
}