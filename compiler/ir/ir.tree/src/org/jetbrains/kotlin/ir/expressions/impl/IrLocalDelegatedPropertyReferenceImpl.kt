/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors
import org.jetbrains.kotlin.ir.expressions.IrLocalDelegatedPropertyReference
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrLocalDelegatedPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrVariableSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrLocalDelegatedPropertyReferenceImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val symbol: IrLocalDelegatedPropertySymbol,
    override val delegate: IrVariableSymbol,
    override val getter: IrSimpleFunctionSymbol,
    override val setter: IrSimpleFunctionSymbol?,
    origin: IrStatementOrigin? = null
) :
    IrNoArgumentsCallableReferenceBase(startOffset, endOffset, type, 0, origin),
    IrLocalDelegatedPropertyReference {

    override val descriptor: VariableDescriptorWithAccessors
        get() = symbol.descriptor

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitLocalDelegatedPropertyReference(this, data)
}