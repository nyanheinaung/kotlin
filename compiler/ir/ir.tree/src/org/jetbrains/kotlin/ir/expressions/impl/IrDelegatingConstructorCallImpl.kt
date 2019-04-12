/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.ir.expressions.IrDelegatingConstructorCall
import org.jetbrains.kotlin.ir.expressions.typeParametersCount
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrDelegatingConstructorCallImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val symbol: IrConstructorSymbol,
    override val descriptor: ClassConstructorDescriptor,
    typeArgumentsCount: Int,
    valueArgumentsCount: Int
) :
    IrCallWithIndexedArgumentsBase(
        startOffset,
        endOffset,
        type,
        typeArgumentsCount = typeArgumentsCount,
        valueArgumentsCount = valueArgumentsCount
    ),
    IrDelegatingConstructorCall {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrConstructorSymbol,
        descriptor: ClassConstructorDescriptor
    ) : this(startOffset, endOffset, type, symbol, descriptor, descriptor.typeParametersCount, descriptor.valueParameters.size)

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrConstructorSymbol
    ) : this(startOffset, endOffset, type, symbol, symbol.descriptor)

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrConstructorSymbol,
        descriptor: ClassConstructorDescriptor,
        typeArgumentsCount: Int
    ) : this(startOffset, endOffset, type, symbol, descriptor, typeArgumentsCount, descriptor.valueParameters.size)

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitDelegatingConstructorCall(this, data)
    }
}
