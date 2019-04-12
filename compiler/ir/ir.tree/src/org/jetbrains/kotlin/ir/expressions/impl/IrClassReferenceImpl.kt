/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.ClassifierDescriptor
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.symbols.IrClassifierSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrClassReferenceImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    symbol: IrClassifierSymbol,
    override val classType: IrType
) :
    IrTerminalDeclarationReferenceBase<IrClassifierSymbol, ClassifierDescriptor>(
        startOffset, endOffset, type,
        symbol, symbol.descriptor
    ),
    IrClassReference {

    override val descriptor: ClassifierDescriptor get() = symbol.descriptor

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitClassReference(this, data)
}
