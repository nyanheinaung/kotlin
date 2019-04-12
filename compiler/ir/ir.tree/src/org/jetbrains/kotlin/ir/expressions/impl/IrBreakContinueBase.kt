/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrBreakContinue
import org.jetbrains.kotlin.ir.expressions.IrLoop
import org.jetbrains.kotlin.ir.types.IrType

abstract class IrBreakContinueBase(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override var loop: IrLoop
) : IrTerminalExpressionBase(startOffset, endOffset, type),
    IrBreakContinue {

    override var label: String? = null
}