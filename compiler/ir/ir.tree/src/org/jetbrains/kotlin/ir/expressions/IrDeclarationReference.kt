/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol


interface IrDeclarationReference : IrExpression {
    val descriptor: DeclarationDescriptor
    val symbol: IrSymbol
}

interface IrGetSingletonValue : IrDeclarationReference {
    override val descriptor: ClassDescriptor
}

interface IrGetObjectValue : IrGetSingletonValue {
    override val symbol: IrClassSymbol
}

interface IrGetEnumValue : IrGetSingletonValue {
    override val symbol: IrEnumEntrySymbol
}

