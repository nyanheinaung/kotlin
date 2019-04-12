/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors
import org.jetbrains.kotlin.ir.symbols.*

interface IrCallableReference : IrMemberAccessExpression, IrDeclarationReference {
    override val descriptor: CallableDescriptor
}

interface IrFunctionReference : IrCallableReference {
    override val descriptor: FunctionDescriptor
    override val symbol: IrFunctionSymbol
}

interface IrPropertyReference : IrCallableReference {
    override val descriptor: PropertyDescriptor
    override val symbol: IrPropertySymbol
    val field: IrFieldSymbol?
    val getter: IrSimpleFunctionSymbol?
    val setter: IrSimpleFunctionSymbol?
}

interface IrLocalDelegatedPropertyReference : IrCallableReference {
    override val descriptor: VariableDescriptorWithAccessors
    override val symbol: IrLocalDelegatedPropertySymbol
    val delegate: IrVariableSymbol
    val getter: IrSimpleFunctionSymbol
    val setter: IrSimpleFunctionSymbol?
}