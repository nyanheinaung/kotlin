/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.builders.buildStatement
import org.jetbrains.kotlin.ir.builders.irIfNull
import org.jetbrains.kotlin.ir.builders.irNull
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockImpl
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.psi2ir.generators.GeneratorWithScope


class SafeCallReceiver(
    val generator: GeneratorWithScope,
    val startOffset: Int,
    val endOffset: Int,
    val extensionReceiver: IntermediateValue?,
    val dispatchReceiver: IntermediateValue?,
    val isStatement: Boolean
) : CallReceiver {

    override fun call(withDispatchAndExtensionReceivers: (IntermediateValue?, IntermediateValue?) -> IrExpression): IrExpression {
        val irTmp = generator.scope.createTemporaryVariable(extensionReceiver?.load() ?: dispatchReceiver!!.load(), "safe_receiver")
        val safeReceiverValue = VariableLValue(generator.context, irTmp)

        val dispatchReceiverValue: IntermediateValue?
        val extensionReceiverValue: IntermediateValue?
        if (extensionReceiver != null) {
            dispatchReceiverValue = dispatchReceiver
            extensionReceiverValue = safeReceiverValue
        } else {
            dispatchReceiverValue = safeReceiverValue
            extensionReceiverValue = null
        }

        val irResult = withDispatchAndExtensionReceivers(dispatchReceiverValue, extensionReceiverValue)

        val resultType = if (isStatement) generator.context.irBuiltIns.unitType else irResult.type.makeNullable()

        val irBlock = IrBlockImpl(startOffset, endOffset, resultType, IrStatementOrigin.SAFE_CALL)

        irBlock.statements.add(irTmp)

        val irIfThenElse =
            generator.buildStatement(startOffset, endOffset, IrStatementOrigin.SAFE_CALL) {
                irIfNull(resultType, safeReceiverValue.load(), irNull(), irResult)
            }
        irBlock.statements.add(irIfThenElse)

        return irBlock
    }
}


fun IrExpression.safeCallOnDispatchReceiver(
    generator: GeneratorWithScope,
    startOffset: Int,
    endOffset: Int,
    ifNotNull: (IrExpression) -> IrExpression
) =
    SafeCallReceiver(
        generator, startOffset, endOffset,
        extensionReceiver = null,
        dispatchReceiver = OnceExpressionValue(this),
        isStatement = false
    ).call { dispatchReceiverValue, _ ->
        ifNotNull(dispatchReceiverValue!!.load())
    }