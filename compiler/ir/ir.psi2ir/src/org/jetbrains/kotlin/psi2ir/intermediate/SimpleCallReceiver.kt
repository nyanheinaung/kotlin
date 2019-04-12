/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.expressions.IrExpression

class SimpleCallReceiver(
    private val dispatchReceiverValue: IntermediateValue?,
    private val extensionReceiverValue: IntermediateValue?
) : CallReceiver {

    override fun call(withDispatchAndExtensionReceivers: (IntermediateValue?, IntermediateValue?) -> IrExpression): IrExpression {
        return withDispatchAndExtensionReceivers(dispatchReceiverValue, extensionReceiverValue)
    }
}
