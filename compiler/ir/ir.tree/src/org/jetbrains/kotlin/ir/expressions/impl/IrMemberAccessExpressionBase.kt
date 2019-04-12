/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrMemberAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

abstract class IrMemberAccessExpressionBase(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    final override val typeArgumentsCount: Int,
    final override val valueArgumentsCount: Int,
    final override val origin: IrStatementOrigin? = null
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrMemberAccessExpression {

    override var dispatchReceiver: IrExpression? = null
    override var extensionReceiver: IrExpression? = null

    private val typeArgumentsByIndex = arrayOfNulls<IrType>(typeArgumentsCount)

    override fun getTypeArgument(index: Int): IrType? {
        if (index >= typeArgumentsCount) {
            throw AssertionError("$this: No such type argument slot: $index")
        }
        return typeArgumentsByIndex[index]
    }

    override fun putTypeArgument(index: Int, type: IrType?) {
        if (index >= typeArgumentsCount) {
            throw AssertionError("$this: No such type argument slot: $index")
        }
        typeArgumentsByIndex[index] = type
    }

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        dispatchReceiver?.accept(visitor, data)
        extensionReceiver?.accept(visitor, data)
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        dispatchReceiver = dispatchReceiver?.transform(transformer, data)
        extensionReceiver = extensionReceiver?.transform(transformer, data)
    }
}