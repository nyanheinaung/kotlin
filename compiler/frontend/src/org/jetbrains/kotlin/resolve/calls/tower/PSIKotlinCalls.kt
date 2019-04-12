/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.tower

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.CallTransformer
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategyForInvoke
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.util.OperatorNameConventions
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

val KotlinCall.psiKotlinCall: PSIKotlinCall
    get() {
        assert(this is PSIKotlinCall) {
            "Incorrect ASTCAll: $this. Java class: ${javaClass.canonicalName}"
        }
        return this as PSIKotlinCall
    }

fun <D : CallableDescriptor> KotlinCall.getResolvedPsiKotlinCall(trace: BindingTrace): NewResolvedCallImpl<D>? =
    psiKotlinCall.psiCall.getResolvedCall(trace.bindingContext) as? NewResolvedCallImpl<D>

abstract class PSIKotlinCall : KotlinCall {
    abstract val psiCall: Call
    abstract val startingDataFlowInfo: DataFlowInfo
    abstract val resultDataFlowInfo: DataFlowInfo
    abstract val dataFlowInfoForArguments: DataFlowInfoForArguments
    abstract val tracingStrategy: TracingStrategy

    override fun toString() = "$psiCall"
}

class PSIKotlinCallImpl(
    override val callKind: KotlinCallKind,
    override val psiCall: Call,
    override val tracingStrategy: TracingStrategy,
    override val explicitReceiver: ReceiverKotlinCallArgument?,
    override val dispatchReceiverForInvokeExtension: ReceiverKotlinCallArgument?,
    override val name: Name,
    override val typeArguments: List<TypeArgument>,
    override val argumentsInParenthesis: List<KotlinCallArgument>,
    override val externalArgument: KotlinCallArgument?,
    override val startingDataFlowInfo: DataFlowInfo,
    override val resultDataFlowInfo: DataFlowInfo,
    override val dataFlowInfoForArguments: DataFlowInfoForArguments
) : PSIKotlinCall()

class PSIKotlinCallForVariable(
    val baseCall: PSIKotlinCallImpl,
    override val explicitReceiver: ReceiverKotlinCallArgument?,
    override val name: Name
) : PSIKotlinCall() {
    override val callKind: KotlinCallKind get() = KotlinCallKind.VARIABLE
    override val typeArguments: List<TypeArgument> get() = emptyList()
    override val argumentsInParenthesis: List<KotlinCallArgument> get() = emptyList()
    override val externalArgument: KotlinCallArgument? get() = null

    override val startingDataFlowInfo: DataFlowInfo get() = baseCall.startingDataFlowInfo
    override val resultDataFlowInfo: DataFlowInfo get() = baseCall.startingDataFlowInfo
    override val dataFlowInfoForArguments: DataFlowInfoForArguments get() = baseCall.dataFlowInfoForArguments

    override val tracingStrategy: TracingStrategy get() = baseCall.tracingStrategy
    override val psiCall: Call = CallTransformer.stripCallArguments(baseCall.psiCall).let {
        if (explicitReceiver == null) CallTransformer.stripReceiver(it) else it
    }
}

class PSIKotlinCallForInvoke(
    val baseCall: PSIKotlinCallImpl,
    val variableCall: KotlinResolutionCandidate,
    override val explicitReceiver: ReceiverKotlinCallArgument,
    override val dispatchReceiverForInvokeExtension: SimpleKotlinCallArgument?
) : PSIKotlinCall() {
    override val callKind: KotlinCallKind get() = KotlinCallKind.FUNCTION
    override val name: Name get() = OperatorNameConventions.INVOKE
    override val typeArguments: List<TypeArgument> get() = baseCall.typeArguments
    override val argumentsInParenthesis: List<KotlinCallArgument> get() = baseCall.argumentsInParenthesis
    override val externalArgument: KotlinCallArgument? get() = baseCall.externalArgument

    override val startingDataFlowInfo: DataFlowInfo get() = baseCall.startingDataFlowInfo
    override val resultDataFlowInfo: DataFlowInfo get() = baseCall.resultDataFlowInfo
    override val dataFlowInfoForArguments: DataFlowInfoForArguments get() = baseCall.dataFlowInfoForArguments
    override val psiCall: Call
    override val tracingStrategy: TracingStrategy

    init {
        val variableReceiver = dispatchReceiverForInvokeExtension ?: explicitReceiver
        val explicitExtensionReceiver = if (dispatchReceiverForInvokeExtension == null) null else explicitReceiver
        val calleeExpression = baseCall.psiCall.calleeExpression!!

        psiCall = CallTransformer.CallForImplicitInvoke(
            explicitExtensionReceiver?.receiverValue,
            variableReceiver.receiverValue as ExpressionReceiver, baseCall.psiCall, true
        )
        tracingStrategy =
                TracingStrategyForInvoke(calleeExpression, psiCall, variableReceiver.receiverValue!!.type) // check for type parameters
    }
}

val ReceiverKotlinCallArgument.receiverValue: ReceiverValue?
    get() = when (this) {
        is SimpleKotlinCallArgument -> this.receiver.receiverValue
        is QualifierReceiverKotlinCallArgument -> this.receiver.classValueReceiver
        else -> null
    }