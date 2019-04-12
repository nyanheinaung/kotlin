/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrErrorCallExpression
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import org.jetbrains.kotlin.utils.SmartList

class IrErrorCallExpressionImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val description: String
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrErrorCallExpression {

    override var explicitReceiver: IrExpression? = null
    override val arguments: MutableList<IrExpression> = SmartList()

    fun addArgument(argument: IrExpression) {
        arguments.add(argument)
    }

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitErrorCallExpression(this, data)
    }

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        explicitReceiver?.accept(visitor, data)
        arguments.forEach { it.accept(visitor, data) }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        explicitReceiver = explicitReceiver?.transform(transformer, data)
        arguments.forEachIndexed { i, irExpression ->
            arguments[i] = irExpression.transform(transformer, data)
        }
    }
}