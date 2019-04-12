/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions

import org.jetbrains.kotlin.ir.declarations.IrReturnTarget
import org.jetbrains.kotlin.ir.declarations.IrSymbolOwner
import org.jetbrains.kotlin.ir.declarations.name
import org.jetbrains.kotlin.ir.symbols.IrFileSymbol
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrReturnableBlockSymbol
import org.jetbrains.kotlin.ir.util.file

interface IrContainerExpression : IrExpression, IrStatementContainer {
    val origin: IrStatementOrigin?
    val isTransparentScope: Boolean
}

interface IrBlock : IrContainerExpression {
    override val isTransparentScope: Boolean
        get() = false
}

interface IrComposite : IrContainerExpression {
    override val isTransparentScope: Boolean
        get() = true
}

interface IrReturnableBlock : IrBlock, IrSymbolOwner, IrReturnTarget {
    override val symbol: IrReturnableBlockSymbol
    val inlineFunctionSymbol: IrFunctionSymbol?
}

val IrReturnableBlock.sourceFileSymbol: IrFileSymbol?
    get() = inlineFunctionSymbol?.owner?.file?.symbol

@Deprecated("Please avoid using it")
val IrReturnableBlock.sourceFileName: String
    get() = sourceFileSymbol?.owner?.name ?: "no source file"

