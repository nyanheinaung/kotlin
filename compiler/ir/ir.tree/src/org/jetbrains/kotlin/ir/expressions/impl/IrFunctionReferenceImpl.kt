/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrFunctionReferenceImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val symbol: IrFunctionSymbol,
    override val descriptor: FunctionDescriptor,
    typeArgumentsCount: Int,
    valueArgumentsCount: Int,
    origin: IrStatementOrigin? = null
) :
    IrCallWithIndexedArgumentsBase(
        startOffset,
        endOffset,
        type,
        typeArgumentsCount,
        valueArgumentsCount,
        origin
    ),
    IrFunctionReference {

    constructor(
        startOffset: Int,
        endOffset: Int,
        type: IrType,
        symbol: IrFunctionSymbol,
        descriptor: FunctionDescriptor,
        typeArgumentsCount: Int,
        origin: IrStatementOrigin? = null
    ) : this(startOffset, endOffset, type, symbol, descriptor, typeArgumentsCount, descriptor.valueParameters.size, origin)

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitFunctionReference(this, data)
}
