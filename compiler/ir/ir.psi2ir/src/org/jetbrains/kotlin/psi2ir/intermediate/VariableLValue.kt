/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrSetVariableImpl
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.symbols.IrVariableSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.utils.addToStdlib.assertedCast

class VariableLValue(
    private val context: IrGeneratorContext,
    val startOffset: Int,
    val endOffset: Int,
    val symbol: IrValueSymbol,
    override val type: IrType,
    val origin: IrStatementOrigin? = null
) :
    LValue,
    AssignmentReceiver {

    constructor(context: IrGeneratorContext, irVariable: IrVariable, origin: IrStatementOrigin? = null) :
            this(context, irVariable.startOffset, irVariable.endOffset, irVariable.symbol, irVariable.type, origin)

    override fun load(): IrExpression =
        IrGetValueImpl(startOffset, endOffset, type, symbol, origin)

    override fun store(irExpression: IrExpression): IrExpression =
        IrSetVariableImpl(
            startOffset, endOffset,
            context.irBuiltIns.unitType,
            symbol.assertedCast<IrVariableSymbol> { "Not a variable: ${symbol.descriptor}" },
            irExpression, origin
        )

    override fun assign(withLValue: (LValue) -> IrExpression): IrExpression =
        withLValue(this)
}