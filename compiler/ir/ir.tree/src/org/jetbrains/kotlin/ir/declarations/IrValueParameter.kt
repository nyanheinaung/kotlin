/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.ParameterDescriptor
import org.jetbrains.kotlin.ir.expressions.IrExpressionBody
import org.jetbrains.kotlin.ir.symbols.IrValueParameterSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

interface IrValueParameter : IrValueDeclaration, IrSymbolDeclaration<IrValueParameterSymbol> {
    override val descriptor: ParameterDescriptor

    val index: Int
    val varargElementType: IrType?
    val isCrossinline: Boolean
    val isNoinline: Boolean

    var defaultValue: IrExpressionBody?

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrValueParameter
}