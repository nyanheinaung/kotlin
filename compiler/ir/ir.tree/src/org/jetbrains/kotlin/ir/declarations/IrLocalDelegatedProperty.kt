/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.VariableDescriptorWithAccessors
import org.jetbrains.kotlin.ir.symbols.IrLocalDelegatedPropertySymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrType

interface IrLocalDelegatedProperty :
    IrDeclarationWithName,
    IrSymbolOwner {

    override val descriptor: VariableDescriptorWithAccessors
    override val symbol: IrLocalDelegatedPropertySymbol

    val type: IrType
    val isVar: Boolean

    var delegate: IrVariable
    var getter: IrFunction
    var setter: IrFunction?
}
