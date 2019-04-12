/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.CallableDescriptor;
import org.jetbrains.kotlin.psi.Call;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.List;

public class InferenceErrorData {
    @NotNull
    public final CallableDescriptor descriptor;
    @NotNull
    public final ConstraintSystem constraintSystem;
    @Nullable
    public final KotlinType receiverArgumentType;
    @NotNull
    public final KotlinType expectedType;
    @NotNull
    public final List<KotlinType> valueArgumentsTypes;
    @NotNull
    public final Call call;

    private InferenceErrorData(
            @NotNull CallableDescriptor descriptor,
            @NotNull ConstraintSystem constraintSystem,
            @NotNull List<KotlinType> valueArgumentsTypes,
            @Nullable KotlinType receiverArgumentType,
            @NotNull KotlinType expectedType,
            @NotNull Call call
    ) {
        this.descriptor = descriptor;
        this.constraintSystem = constraintSystem;
        this.receiverArgumentType = receiverArgumentType;
        this.valueArgumentsTypes = valueArgumentsTypes;
        this.expectedType = expectedType;
        this.call = call;
    }

    @NotNull
    public static InferenceErrorData create(
            @NotNull CallableDescriptor descriptor,
            @NotNull ConstraintSystem constraintSystem,
            @NotNull List<KotlinType> valueArgumentsTypes,
            @Nullable KotlinType receiverArgumentType,
            @NotNull KotlinType expectedType,
            @NotNull Call call
    ) {
        return new InferenceErrorData(descriptor, constraintSystem, valueArgumentsTypes, receiverArgumentType, expectedType, call);
    }
}
