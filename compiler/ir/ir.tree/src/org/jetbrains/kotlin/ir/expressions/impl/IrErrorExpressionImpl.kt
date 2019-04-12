/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrErrorExpression
import org.jetbrains.kotlin.ir.expressions.IrExpressionWithCopy
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrErrorExpressionImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val description: String
) :
    IrTerminalExpressionBase(startOffset, endOffset, type),
    IrExpressionWithCopy,
    IrErrorExpression {

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitErrorExpression(this, data)

    override fun copy(): IrErrorExpressionImpl =
        IrErrorExpressionImpl(startOffset, endOffset, type, description)
}