/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.ir.symbols.IrTypeParameterSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.types.Variance

interface IrTypeParameter : IrSymbolDeclaration<IrTypeParameterSymbol>, IrDeclarationWithName {
    override val descriptor: TypeParameterDescriptor

    val variance: Variance
    val index: Int
    val isReified: Boolean
    val superTypes: MutableList<IrType>

    override fun <D> transform(transformer: IrElementTransformer<D>, data: D): IrTypeParameter
}