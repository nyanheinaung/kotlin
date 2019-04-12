/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.builders.IrGeneratorContext
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrGetFieldImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrSetFieldImpl
import org.jetbrains.kotlin.ir.symbols.IrFieldSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.types.KotlinType

class BackingFieldLValue(
    private val context: IrGeneratorContext,
    private val startOffset: Int,
    private val endOffset: Int,
    override val type: IrType,
    private val symbol: IrFieldSymbol,
    private val receiver: IntermediateValue?,
    private val origin: IrStatementOrigin?
) : LValue, AssignmentReceiver {

    override fun store(irExpression: IrExpression): IrExpression =
        IrSetFieldImpl(startOffset, endOffset, symbol, receiver?.load(), irExpression, context.irBuiltIns.unitType, origin)

    override fun load(): IrExpression =
        IrGetFieldImpl(startOffset, endOffset, symbol, type, receiver?.load(), origin)

    override fun assign(withLValue: (LValue) -> IrExpression): IrExpression =
        withLValue(this)
}
