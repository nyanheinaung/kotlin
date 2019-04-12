/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi2ir.intermediate

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.psi2ir.isValueArgumentReorderingRequired
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.types.KotlinType

class CallBuilder(
    val original: ResolvedCall<*>, // TODO get rid of "original", sometimes we want to generate a call without ResolvedCall
    val descriptor: CallableDescriptor,
    val typeArguments: Map<TypeParameterDescriptor, KotlinType>?,
    val isExtensionInvokeCall: Boolean = false
) {
    var superQualifier: ClassDescriptor? = null

    lateinit var callReceiver: CallReceiver

    private val parametersOffset = if (isExtensionInvokeCall) 1 else 0

    val irValueArgumentsByIndex = arrayOfNulls<IrExpression>(descriptor.valueParameters.size)

    fun getValueArgument(valueParameterDescriptor: ValueParameterDescriptor) =
        irValueArgumentsByIndex[valueParameterDescriptor.index + parametersOffset]
}

val CallBuilder.argumentsCount: Int
    get() =
        irValueArgumentsByIndex.size

var CallBuilder.lastArgument: IrExpression?
    get() = irValueArgumentsByIndex.last()
    set(value) {
        irValueArgumentsByIndex[argumentsCount - 1] = value
    }

fun CallBuilder.getValueArgumentsInParameterOrder(): List<IrExpression?> =
    descriptor.valueParameters.map { irValueArgumentsByIndex[it.index] }

fun CallBuilder.isValueArgumentReorderingRequired() =
    original.isValueArgumentReorderingRequired()

val CallBuilder.hasExtensionReceiver: Boolean
    get() =
        descriptor.extensionReceiverParameter != null

val CallBuilder.hasDispatchReceiver: Boolean
    get() =
        descriptor.dispatchReceiverParameter != null

val CallBuilder.extensionReceiverType: KotlinType?
    get() =
        descriptor.extensionReceiverParameter?.type

val CallBuilder.dispatchReceiverType: KotlinType?
    get() =
        descriptor.dispatchReceiverParameter?.type

val CallBuilder.explicitReceiverParameter: ReceiverParameterDescriptor?
    get() =
        descriptor.extensionReceiverParameter ?: descriptor.dispatchReceiverParameter

val CallBuilder.explicitReceiverType: KotlinType?
    get() =
        explicitReceiverParameter?.type

fun CallBuilder.setExplicitReceiverValue(explicitReceiverValue: IntermediateValue) {
    val previousCallReceiver = callReceiver
    callReceiver = object : CallReceiver {
        override fun call(withDispatchAndExtensionReceivers: (IntermediateValue?, IntermediateValue?) -> IrExpression): IrExpression {
            return previousCallReceiver.call { dispatchReceiverValue, _ ->
                val newDispatchReceiverValue = if (hasExtensionReceiver) dispatchReceiverValue else explicitReceiverValue
                val newExtensionReceiverValue = if (hasExtensionReceiver) explicitReceiverValue else null
                withDispatchAndExtensionReceivers(newDispatchReceiverValue, newExtensionReceiverValue)
            }
        }
    }
}
