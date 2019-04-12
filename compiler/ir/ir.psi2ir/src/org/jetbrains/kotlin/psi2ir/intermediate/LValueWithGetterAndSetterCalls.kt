/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.psi2ir.generators.CallGenerator

class LValueWithGetterAndSetterCalls(
    val callGenerator: CallGenerator,
    val descriptor: CallableDescriptor,
    val getterCall: () -> CallBuilder?,
    val setterCall: (IrExpression) -> CallBuilder?,
    override val type: IrType,
    val startOffset: Int,
    val endOffset: Int,
    val origin: IrStatementOrigin? = null
) : LValue {

    override fun load(): IrExpression {
        val call = getterCall() ?: throw AssertionError("No getter call for $descriptor")
        return callGenerator.generateCall(startOffset, endOffset, call, origin)
    }

    override fun store(irExpression: IrExpression): IrExpression {
        val call = setterCall(irExpression) ?: throw AssertionError("No setter call for $descriptor")
        return callGenerator.generateCall(startOffset, endOffset, call, origin)
    }

}
