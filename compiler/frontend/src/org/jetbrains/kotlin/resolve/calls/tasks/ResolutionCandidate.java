/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.tasks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.CallableDescriptor;
import org.jetbrains.kotlin.psi.Call;
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue;
import org.jetbrains.kotlin.types.TypeSubstitutor;

public class ResolutionCandidate<D extends CallableDescriptor> {
    private final Call call;
    private final D candidateDescriptor;
    private final TypeSubstitutor knownTypeParametersResultingSubstitutor;
    private ReceiverValue dispatchReceiver; // receiver object of a method
    private ExplicitReceiverKind explicitReceiverKind;

    private ResolutionCandidate(
            @NotNull Call call, @NotNull D descriptor, @Nullable ReceiverValue dispatchReceiver,
            @NotNull ExplicitReceiverKind explicitReceiverKind,
            @Nullable TypeSubstitutor knownTypeParametersResultingSubstitutor
    ) {
        this.call = call;
        this.candidateDescriptor = descriptor;
        this.dispatchReceiver = dispatchReceiver;
        this.explicitReceiverKind = explicitReceiverKind;
        this.knownTypeParametersResultingSubstitutor = knownTypeParametersResultingSubstitutor;
    }

    public static <D extends CallableDescriptor> ResolutionCandidate<D> create(
            @NotNull Call call, @NotNull D descriptor
    ) {
        return new ResolutionCandidate<>(call, descriptor, null, ExplicitReceiverKind.NO_EXPLICIT_RECEIVER, null);
    }

    public static <D extends CallableDescriptor> ResolutionCandidate<D> create(
            @NotNull Call call, @NotNull D descriptor, @Nullable TypeSubstitutor knownTypeParametersResultingSubstitutor
    ) {
        return new ResolutionCandidate<>(call, descriptor, null, ExplicitReceiverKind.NO_EXPLICIT_RECEIVER,
                                         knownTypeParametersResultingSubstitutor);
    }

    public static <D extends CallableDescriptor> ResolutionCandidate<D> create(
            @NotNull Call call, @NotNull D descriptor, @Nullable ReceiverValue dispatchReceiver,
            @NotNull ExplicitReceiverKind explicitReceiverKind,
            @Nullable TypeSubstitutor knownTypeParametersResultingSubstitutor
    ) {
        return new ResolutionCandidate<>(call, descriptor, dispatchReceiver, explicitReceiverKind, knownTypeParametersResultingSubstitutor);
    }

    public void setDispatchReceiver(@Nullable ReceiverValue dispatchReceiver) {
        this.dispatchReceiver = dispatchReceiver;
    }

    public void setExplicitReceiverKind(@NotNull ExplicitReceiverKind explicitReceiverKind) {
        this.explicitReceiverKind = explicitReceiverKind;
    }

    @NotNull
    public Call getCall() {
        return call;
    }

    @NotNull
    public D getDescriptor() {
        return candidateDescriptor;
    }

    @Nullable
    public ReceiverValue getDispatchReceiver() {
        return dispatchReceiver;
    }

    @NotNull
    public ExplicitReceiverKind getExplicitReceiverKind() {
        return explicitReceiverKind;
    }

    @Nullable
    public TypeSubstitutor getKnownTypeParametersResultingSubstitutor() {
        return knownTypeParametersResultingSubstitutor;
    }

    @Override
    public String toString() {
        return candidateDescriptor.toString();
    }
}
