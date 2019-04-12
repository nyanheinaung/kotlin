/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.declarations

import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrVariableSymbol

interface IrVariable : IrValueDeclaration, IrSymbolDeclaration<IrVariableSymbol> {
    override val descriptor: VariableDescriptor

    val isVar: Boolean
    val isConst: Boolean
    val isLateinit: Boolean

    var initializer: IrExpression?
}

