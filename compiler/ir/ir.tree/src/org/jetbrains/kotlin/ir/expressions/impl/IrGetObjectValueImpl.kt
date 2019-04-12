/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.ir.expressions.IrGetObjectValue
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrGetObjectValueImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    symbol: IrClassSymbol
) :
    IrTerminalDeclarationReferenceBase<IrClassSymbol, ClassDescriptor>(startOffset, endOffset, type, symbol, symbol.descriptor),
    IrGetObjectValue {

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R =
        visitor.visitGetObjectValue(this, data)
}
