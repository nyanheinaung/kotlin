/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

interface IrBody : IrElement {
    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrBody =
        accept(transformer, data) as IrBody
}

interface IrExpressionBody : IrBody {
    var expression: IrExpression

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrExpressionBody =
        accept(transformer, data) as IrExpressionBody
}

interface IrBlockBody : IrBody, IrStatementContainer

interface IrSyntheticBody : IrBody {
    val kind: IrSyntheticBodyKind
}

enum class IrSyntheticBodyKind {
    ENUM_VALUES,
    ENUM_VALUEOF
}

