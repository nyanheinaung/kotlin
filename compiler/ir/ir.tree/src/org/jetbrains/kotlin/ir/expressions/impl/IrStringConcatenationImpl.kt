/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStringConcatenation
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import java.util.*

class IrStringConcatenationImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrStringConcatenation {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        arguments: Collection<IrExpression>
    ) : this(startOffset, endOffset, type) {
        this.arguments.addAll(arguments)
    }

    override val arguments: MutableList<IrExpression> = ArrayList()

    override fun addArgument(argument: IrExpression) {
        arguments.add(argument)
    }

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitStringConcatenation(this, data)

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        arguments.forEach { it.accept(visitor, data) }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        arguments.forEachIndexed { i, irExpression ->
            arguments[i] = irExpression.transform(transformer, data)
        }
    }
}