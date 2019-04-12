/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrCallableReference
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.types.IrType

abstract class IrNoArgumentsCallableReferenceBase(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    typeArgumentsCount: Int,
    origin: IrStatementOrigin? = null
) :
    IrMemberAccessExpressionBase(startOffset, endOffset, type, typeArgumentsCount, 0, origin),
    IrCallableReference {

    private fun throwNoValueArguments(): Nothing {
        throw UnsupportedOperationException("Property reference $descriptor has no value arguments")
    }

    override fun getValueArgument(index: Int): IrExpression? = throwNoValueArguments()

    override fun putValueArgument(index: Int, valueArgument: IrExpression?) = throwNoValueArguments()

    override fun removeValueArgument(index: Int) = throwNoValueArguments()
}

