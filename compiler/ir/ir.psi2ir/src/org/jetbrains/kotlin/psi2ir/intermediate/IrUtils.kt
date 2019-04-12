/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl

fun IrVariable.defaultLoad(): IrExpression =
    IrGetValueImpl(startOffset, endOffset, type, symbol)

fun CallReceiver.adjustForCallee(callee: CallableMemberDescriptor): CallReceiver =
    object : CallReceiver {
        override fun call(withDispatchAndExtensionReceivers: (IntermediateValue?, IntermediateValue?) -> IrExpression): IrExpression =
            this@adjustForCallee.call { dispatchReceiverValue, extensionReceiverValue ->
                val numReceiversPresent = listOfNotNull(dispatchReceiverValue, extensionReceiverValue).size
                val numReceiversExpected = listOfNotNull(callee.dispatchReceiverParameter, callee.extensionReceiverParameter).size
                if (numReceiversPresent != numReceiversExpected)
                    throw AssertionError("Mismatching receivers for $callee: $numReceiversPresent, expected: $numReceiversExpected")

                val newDispatchReceiverValue =
                    when {
                        callee.dispatchReceiverParameter == null -> null
                        dispatchReceiverValue != null -> dispatchReceiverValue
                        else -> extensionReceiverValue
                    }
                val newExtensionReceiverValue =
                    when {
                        callee.extensionReceiverParameter == null -> null
                        dispatchReceiverValue != null && callee.dispatchReceiverParameter == null -> dispatchReceiverValue
                        else -> extensionReceiverValue
                    }
                withDispatchAndExtensionReceivers(newDispatchReceiverValue, newExtensionReceiverValue)
            }
    }