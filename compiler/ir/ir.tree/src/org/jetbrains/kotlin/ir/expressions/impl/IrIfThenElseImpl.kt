/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.expressions.impl

import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.utils.SmartList

class IrIfThenElseImpl(
    startOffset: Int,
    endOffset: Int,
    type: IrType,
    override val origin: IrStatementOrigin? = null
) :
    IrWhenBase(startOffset, endOffset, type) {

    override val branches: MutableList<IrBranch> = SmartList()
}