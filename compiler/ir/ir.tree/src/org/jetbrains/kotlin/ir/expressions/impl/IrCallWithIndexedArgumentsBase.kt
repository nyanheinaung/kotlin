/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

abstract class IrCallWithIndexedArgumentsBase(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    typeArgumentsCount: Int,
    valueArgumentsCount: Int,
    origin: IrStatementOrigin? = null
) :
    IrMemberAccessExpressionBase(
        startOffset,
        endOffset,
        type,
        typeArgumentsCount,
        valueArgumentsCount,
        origin
    ) {

    private val argumentsByParameterIndex: Array<IrExpression?> = arrayOfNulls(valueArgumentsCount)

    override fun getValueArgument(index: Int): IrExpression? {
        if (index >= valueArgumentsCount) {
            throw AssertionError("$this: No such value argument slot: $index")
        }
        return argumentsByParameterIndex[index]
    }

    override fun putValueArgument(index: Int, valueArgument: IrExpression?) {
        if (index >= valueArgumentsCount) {
            throw AssertionError("$this: No such value argument slot: $index")
        }
        argumentsByParameterIndex[index] = valueArgument
    }

    override fun removeValueArgument(index: Int) {
        argumentsByParameterIndex[index] = null
    }

    override fun <D> acceptChildren(visitor: IrElementVisitor<Unit, D>, data: D) {
        super.acceptChildren(visitor, data)
        argumentsByParameterIndex.forEach { it?.accept(visitor, data) }
    }

    override fun <D> transformChildren(transformer: IrElementTransformer<D>, data: D) {
        super.transformChildren(transformer, data)
        argumentsByParameterIndex.forEachIndexed { i, irExpression ->
            argumentsByParameterIndex[i] = irExpression?.transform(transformer, data)
        }
    }
}