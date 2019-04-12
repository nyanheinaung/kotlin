/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.types.KotlinType

interface IntermediateValue {
    fun load(): IrExpression
    fun loadIfExists(): IrExpression? = load()
    val type: IrType
}

interface LValue : IntermediateValue {
    fun store(irExpression: IrExpression): IrExpression
}

interface AssignmentReceiver {
    fun assign(withLValue: (LValue) -> IrExpression): IrExpression
    fun assign(value: IrExpression): IrExpression = assign { it.store(value) }
}

interface CallReceiver {
    fun call(withDispatchAndExtensionReceivers: (IntermediateValue?, IntermediateValue?) -> IrExpression): IrExpression
}
