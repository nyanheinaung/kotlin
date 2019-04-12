/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.descriptors.ValueDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.symbols.IrVariableSymbol

interface IrValueAccessExpression : IrDeclarationReference {
    override val descriptor: ValueDescriptor
    override val symbol: IrValueSymbol
    val origin: IrStatementOrigin?
}

interface IrGetValue : IrValueAccessExpression, IrExpressionWithCopy {
    override fun copy(): IrGetValue
}

interface IrSetVariable : IrValueAccessExpression {
    override val descriptor: VariableDescriptor
    override val symbol: IrVariableSymbol
    var value: IrExpression
}

