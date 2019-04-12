/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.ClassConstructorDescriptor
import org.jetbrains.kotlin.ir.expressions.IrEnumConstructorCall
import org.jetbrains.kotlin.ir.expressions.typeParametersCount
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrEnumConstructorCallImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val symbol: IrConstructorSymbol,
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
    IrEnumConstructorCall {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrConstructorSymbol
    ) : this(startOffset, endOffset, type, symbol, symbol.descriptor.typeParametersCount, symbol.descriptor.valueParameters.size)

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrConstructorSymbol,
        typeArgumentsCount: Int
    ) : this(startOffset, endOffset, type, symbol, typeArgumentsCount, symbol.descriptor.valueParameters.size)

    override val descriptor: ClassConstructorDescriptor get() = symbol.descriptor

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitEnumConstructorCall(this, data)
    }
}
