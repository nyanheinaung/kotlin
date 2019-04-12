/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.VariableDescriptor
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace
import org.jetbrains.kotlin.resolve.calls.results.ResolutionStatus

interface VariableAsFunctionResolvedCall {
    val functionCall: ResolvedCall<FunctionDescriptor>
    val variableCall: ResolvedCall<VariableDescriptor>
}

interface VariableAsFunctionMutableResolvedCall : VariableAsFunctionResolvedCall {
    override val functionCall: MutableResolvedCall<FunctionDescriptor>
    override val variableCall: MutableResolvedCall<VariableDescriptor>
}

class VariableAsFunctionResolvedCallImpl(
    override val functionCall: MutableResolvedCall<FunctionDescriptor>,
    override val variableCall: MutableResolvedCall<VariableDescriptor>
) : VariableAsFunctionMutableResolvedCall, MutableResolvedCall<FunctionDescriptor> by functionCall {

    override fun markCallAsCompleted() {
        functionCall.markCallAsCompleted()
        variableCall.markCallAsCompleted()
    }

    override fun isCompleted(): Boolean = functionCall.isCompleted && variableCall.isCompleted

    override fun getStatus(): ResolutionStatus = variableCall.status.combine(functionCall.status)

    override fun getTrace(): DelegatingBindingTrace {
        return functionCall.trace
    }

}
