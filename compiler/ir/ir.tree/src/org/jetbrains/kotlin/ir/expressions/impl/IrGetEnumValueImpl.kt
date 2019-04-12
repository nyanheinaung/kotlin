/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class IrGetEnumValueImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    symbol: IrEnumEntrySymbol
) :
    IrTerminalDeclarationReferenceBase<IrEnumEntrySymbol, ClassDescriptor>(startOffset, endOffset, type, symbol, symbol.descriptor),
    IrGetEnumValue {

    override fun <R, D> accept(visitor: IrElementVisitor<R, D>, data: D): R {
        return visitor.visitGetEnumValue(this, data)
    }
}
