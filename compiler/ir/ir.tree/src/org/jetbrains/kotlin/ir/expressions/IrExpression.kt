/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

interface IrExpression : IrStatement, IrVarargElement {
    val type: IrType

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrExpression =
        accept(transformer, data) as IrExpression
}

interface IrExpressionWithCopy : IrExpression {
    fun copy(): IrExpressionWithCopy
}

