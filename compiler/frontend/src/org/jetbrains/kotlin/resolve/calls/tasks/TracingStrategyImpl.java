/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.tasks;

import kotlin.collections.CollectionsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.FunctionTypesKt;
import org.jetbrains.kotlin.descriptors.CallableDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.FunctionDescriptor;
import org.jetbrains.kotlin.descriptors.VariableDescriptor;
import org.jetbrains.kotlin.diagnostics.Errors;
import org.jetbrains.kotlin.psi.Call;
import org.jetbrains.kotlin.psi.KtReferenceExpression;
import org.jetbrains.kotlin.resolve.BindingTrace;
import org.jetbrains.kotlin.resolve.calls.callResolverUtil.CallResolverUtilKt;
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall;
import org.jetbrains.kotlin.resolve.calls.model.VariableAsFunctionResolvedCall;
import org.jetbrains.kotlin.resolve.calls.util.FakeCallableDescriptorForObject;
import org.jetbrains.kotlin.types.ErrorUtils;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.Collection;
import java.util.List;

import static org.jetbrains.kotlin.diagnostics.Errors.UNRESOLVED_REFERENCE;
import static org.jetbrains.kotlin.diagnostics.Errors.UNRESOLVED_REFERENCE_WRONG_RECEIVER;
import static org.jetbrains.kotlin.resolve.BindingContext.*;

public class TracingStrategyImpl extends AbstractTracingStrategy {
    private final KtReferenceExpression reference;

    private TracingStrategyImpl(@NotNull KtReferenceExpression reference, @NotNull Call call) {
        super(reference, call);
        this.reference = reference;
    }

    @NotNull
    public static TracingStrategy create(@NotNull KtReferenceExpression reference, @NotNull Call call) {
        return new TracingStrategyImpl(reference, call);
    }

    @Override
    public void bindCall(@NotNull BindingTrace trace, @NotNull Call call) {
        trace.record(CALL, call.getCalleeExpression(), call);
    }

    @Override
    public <D extends CallableDescriptor> void bindReference(@NotNull BindingTrace trace, @NotNull ResolvedCall<D> resolvedCall) {
        DeclarationDescriptor descriptor = resolvedCall.getCandidateDescriptor();
        if (resolvedCall instanceof VariableAsFunctionResolvedCall) {
            descriptor = ((VariableAsFunctionResolvedCall) resolvedCall).getVariableCall().getCandidateDescriptor();
        }
        if (descriptor instanceof FakeCallableDescriptorForObject) {
            FakeCallableDescriptorForObject fakeCallableDescriptorForObject = (FakeCallableDescriptorForObject) descriptor;
            descriptor = fakeCallableDescriptorForObject.getReferencedDescriptor();
            if (fakeCallableDescriptorForObject.getClassDescriptor().getCompanionObjectDescriptor() != null) {
                trace.record(SHORT_REFERENCE_TO_COMPANION_OBJECT, reference, fakeCallableDescriptorForObject.getClassDescriptor());
            }
        }
        DeclarationDescriptor storedReference = trace.get(REFERENCE_TARGET, reference);
        if (storedReference == null || !ErrorUtils.isError(descriptor)) {
            trace.record(REFERENCE_TARGET, reference, descriptor);
        }
    }

    @Override
    public <D extends CallableDescriptor> void bindResolvedCall(@NotNull BindingTrace trace, @NotNull ResolvedCall<D> resolvedCall) {
        trace.record(RESOLVED_CALL, call, resolvedCall);
    }

    @Override
    public void unresolvedReference(@NotNull BindingTrace trace) {
        trace.report(UNRESOLVED_REFERENCE.on(reference, reference));
    }

    @Override
    public <D extends CallableDescriptor> void unresolvedReferenceWrongReceiver(@NotNull BindingTrace trace, @NotNull Collection<? extends ResolvedCall<D>> candidates) {
        VariableDescriptor variableDescriptor = isFunctionExpectedError(candidates);
        if (variableDescriptor != null) {
            trace.report(Errors.FUNCTION_EXPECTED.on(reference, reference, variableDescriptor.getType()));
        }
        else {
            trace.report(UNRESOLVED_REFERENCE_WRONG_RECEIVER.on(reference, candidates));
        }
    }

    @Nullable
    private static <D extends CallableDescriptor> VariableDescriptor isFunctionExpectedError(
            @NotNull Collection<? extends ResolvedCall<D>> candidates
    ) {
        List<VariableDescriptor> variables = CollectionsKt.map(candidates, TracingStrategyImpl::variableIfFunctionExpectedError);
        List<VariableDescriptor> distinctVariables = CollectionsKt.distinct(variables);
        return CollectionsKt.singleOrNull(distinctVariables);
    }

    @Nullable
    private static <D extends CallableDescriptor> VariableDescriptor variableIfFunctionExpectedError(
            @NotNull ResolvedCall<D> candidate
    ) {
        if (!(candidate instanceof VariableAsFunctionResolvedCall)) return null;

        ResolvedCall<VariableDescriptor> variableCall = ((VariableAsFunctionResolvedCall) candidate).getVariableCall();
        ResolvedCall<FunctionDescriptor> functionCall = ((VariableAsFunctionResolvedCall) candidate).getFunctionCall();

        KotlinType type = variableCall.getCandidateDescriptor().getType();

        boolean nonFunctionalVar = variableCall.getStatus().isSuccess() && !FunctionTypesKt.isFunctionType(type);
        Call functionPsiCall = functionCall.getCall();
        if (nonFunctionalVar && CallResolverUtilKt.isInvokeCallOnVariable(functionPsiCall) && functionPsiCall.getValueArguments().isEmpty()) {
            return variableCall.getCandidateDescriptor();
        }

        return null;
    }
}
