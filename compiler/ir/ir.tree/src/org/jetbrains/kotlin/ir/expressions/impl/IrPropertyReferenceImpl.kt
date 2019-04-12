/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrFieldSymbol
import org.jetbrains.kotlin.ir.symbols.IrPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrPropertySymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrPropertyReferenceImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val symbol: IrPropertySymbol,
    typeArgumentsCount: Int,
    override val field: IrFieldSymbol?,
    override val getter: IrSimpleFunctionSymbol?,
    override val setter: IrSimpleFunctionSymbol?,
    origin: IrStatementOrigin? = null
) :
    IrNoArgumentsCallableReferenceBase(startOffset, endOffset, type, typeArgumentsCount, origin),
    IrPropertyReference {

    @Deprecated(message = "Don't use descriptor-based API for IrPropertyReference", level = DeprecationLevel.WARNING)
    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        descriptor: PropertyDescriptor,
        typeArgumentsCount: Int,
        field: IrFieldSymbol?,
        getter: IrSimpleFunctionSymbol?,
        setter: IrSimpleFunctionSymbol?,
        origin: IrStatementOrigin? = null
    ) : this(
        startOffset, endOffset, type,
        IrPropertySymbolImpl(descriptor),
        typeArgumentsCount, field, getter, setter, origin
    )

    override val descriptor: PropertyDescriptor
        get() = symbol.descriptor

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitPropertyReference(this, data)
}