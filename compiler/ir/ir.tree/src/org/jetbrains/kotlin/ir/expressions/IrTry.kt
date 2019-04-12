/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

interface IrTry : IrExpression {
    var tryResult: IrExpression

    val catches: List<IrCatch>

    var finallyExpression: IrExpression?
}

interface IrCatch : IrElement {
    val parameter: VariableDescriptor
    var catchParameter: IrVariable
    var result: IrExpression

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrCatch =
        super.transform(transformer, data) as IrCatch
}
