/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.CallableDescriptor;
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor;
import org.jetbrains.kotlin.psi.ValueArgument;
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace;
import org.jetbrains.kotlin.resolve.calls.inference.ConstraintSystem;
import org.jetbrains.kotlin.resolve.calls.results.ResolutionStatus;
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeSubstitutor;

public interface MutableResolvedCall<D extends CallableDescriptor> extends ResolvedCall<D>  {

    void addStatus(@NotNull ResolutionStatus status);

    void setStatusToSuccess();

    @NotNull
    DelegatingBindingTrace getTrace();

    @NotNull
    TracingStrategy getTracingStrategy();

    void markCallAsCompleted();

    void addRemainingTasks(Function0<Unit> task);

    void performRemainingTasks();

    boolean isCompleted();


    void recordValueArgument(@NotNull ValueParameterDescriptor valueParameter, @NotNull ResolvedValueArgument valueArgument);

    void recordArgumentMatchStatus(@NotNull ValueArgument valueArgument, @NotNull ArgumentMatchStatus matchStatus);

    @Override
    @NotNull
    MutableDataFlowInfoForArguments getDataFlowInfoForArguments();

    @Nullable
    ConstraintSystem getConstraintSystem();

    void setConstraintSystem(@NotNull ConstraintSystem constraintSystem);

    void setResultingSubstitutor(@NotNull TypeSubstitutor substitutor);

    @Nullable
    TypeSubstitutor getKnownTypeParametersSubstitutor();

    //todo remove: use value to parameter map status
    boolean hasInferredReturnType();

    void setSmartCastDispatchReceiverType(@NotNull KotlinType smartCastDispatchReceiverType);

    void updateExtensionReceiverWithSmartCastIfNeeded(@NotNull KotlinType smartCastExtensionReceiverType);
}
