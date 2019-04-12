/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.expressions.IrExpression

class ExtensionInvokeCallReceiver(
    private val callBuilder: CallBuilder,
    private val functionReceiver: IntermediateValue,
    private val extensionInvokeReceiver: IntermediateValue
) : CallReceiver {

    override fun call(withDispatchAndExtensionReceivers: (IntermediateValue?, IntermediateValue?) -> IrExpression): IrExpression {
        // extensionInvokeReceiver is actually a first argument:
        //      receiver.extFun(p1, ..., pN)
        //      =>
        //      extFun.invoke(receiver, p1, ..., pN)

        assert(callBuilder.irValueArgumentsByIndex[0] == null) {
            "Extension 'invoke' call should have null as its 1st value argument, got: ${callBuilder.irValueArgumentsByIndex[0]}"
        }
        callBuilder.irValueArgumentsByIndex[0] = extensionInvokeReceiver.load()
        return withDispatchAndExtensionReceivers(functionReceiver, null)
    }
}