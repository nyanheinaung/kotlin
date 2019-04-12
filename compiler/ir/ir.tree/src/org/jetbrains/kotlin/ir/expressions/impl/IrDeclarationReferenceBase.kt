/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.ir.expressions.IrDeclarationReference
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType

abstract class IrDeclarationReferenceBase<out S : IrSymbol, out D : DeclarationDescriptor>(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val symbol: S,
    override val descriptor: D
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrDeclarationReference