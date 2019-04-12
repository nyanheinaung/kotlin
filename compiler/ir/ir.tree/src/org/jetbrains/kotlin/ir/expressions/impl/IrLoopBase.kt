/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrLoop
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.types.IrType

abstract class IrLoopBase(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val origin: IrStatementOrigin?
) :
    IrExpressionBase(startOffset, endOffset, type),
    IrLoop {

    override var label: String? = null

    override lateinit var condition: IrExpression

    override var body: IrExpression? = null
}