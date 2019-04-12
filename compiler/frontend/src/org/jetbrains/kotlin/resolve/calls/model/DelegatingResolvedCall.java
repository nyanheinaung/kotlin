/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.CallableDescriptor;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor;
import org.jetbrains.kotlin.psi.Call;
import org.jetbrains.kotlin.psi.ValueArgument;
import org.jetbrains.kotlin.resolve.calls.results.ResolutionStatus;
import org.jetbrains.kotlin.resolve.calls.tasks.ExplicitReceiverKind;
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.List;
import java.util.Map;

public abstract class DelegatingResolvedCall<D extends CallableDescriptor> implements ResolvedCall<D> {
    private final ResolvedCall<? extends D> resolvedCall;

    public DelegatingResolvedCall(@NotNull ResolvedCall<? extends D> resolvedCall) {
        this.resolvedCall = resolvedCall;
    }

    @NotNull
    @Override
    public ResolutionStatus getStatus() {
        return resolvedCall.getStatus();
    }

    @NotNull
    @Override
    public Call getCall() {
        return resolvedCall.getCall();
    }

    @NotNull
    @Override
    public D getCandidateDescriptor() {
        return resolvedCall.getCandidateDescriptor();
    }

    @NotNull
    @Override
    public D getResultingDescriptor() {
        return resolvedCall.getResultingDescriptor();
    }

    @Nullable
    @Override
    public ReceiverValue getExtensionReceiver() {
        return resolvedCall.getExtensionReceiver();
    }

    @Nullable
    @Override
    public ReceiverValue getDispatchReceiver() {
        return resolvedCall.getDispatchReceiver();
    }

    @NotNull
    @Override
    public ExplicitReceiverKind getExplicitReceiverKind() {
        return resolvedCall.getExplicitReceiverKind();
    }

    @NotNull
    @Override
    public Map<ValueParameterDescriptor, ResolvedValueArgument> getValueArguments() {
        return resolvedCall.getValueArguments();
    }

    @NotNull
    @Override
    public ArgumentMapping getArgumentMapping(@NotNull ValueArgument valueArgument) {
        return resolvedCall.getArgumentMapping(valueArgument);
    }

    @Nullable
    @Override
    public List<ResolvedValueArgument> getValueArgumentsByIndex() {
        return resolvedCall.getValueArgumentsByIndex();
    }

    @NotNull
    @Override
    public Map<TypeParameterDescriptor, KotlinType> getTypeArguments() {
        return resolvedCall.getTypeArguments();
    }

    @NotNull
    @Override
    public DataFlowInfoForArguments getDataFlowInfoForArguments() {
        return resolvedCall.getDataFlowInfoForArguments();
    }

    @Nullable
    @Override
    public KotlinType getSmartCastDispatchReceiverType() {
        return resolvedCall.getSmartCastDispatchReceiverType();
    }
}
