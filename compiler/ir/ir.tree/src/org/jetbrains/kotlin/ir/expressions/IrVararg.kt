/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

interface IrVarargElement : IrElement

interface IrVararg : IrExpression {
    val varargElementType: IrType

    val elements: List<IrVarargElement>

    fun putElement(i: Int, element: IrVarargElement)
}

interface IrSpreadElement : IrVarargElement {
    var expression: IrExpression

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrElement =
        accept(transformer, data) as IrSpreadElement
}

