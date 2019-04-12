/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.components;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor;
import org.jetbrains.kotlin.load.java.structure.JavaMethod;
import org.jetbrains.kotlin.resolve.BindingTrace;
import org.jetbrains.kotlin.resolve.jvm.JvmBindingContextSlices;
import org.jetbrains.kotlin.resolve.jvm.kotlinSignature.SignaturesPropagationData;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.List;

public class SignaturePropagatorImpl implements SignaturePropagator {
    private final BindingTrace trace;

    public SignaturePropagatorImpl(@NotNull BindingTrace trace) {
        this.trace = trace;
    }

    @Override
    @NotNull
    public PropagatedSignature resolvePropagatedSignature(
            @NotNull JavaMethod method,
            @NotNull ClassDescriptor owner,
            @NotNull KotlinType returnType,
            @Nullable KotlinType receiverType,
            @NotNull List<ValueParameterDescriptor> valueParameters,
            @NotNull List<TypeParameterDescriptor> typeParameters
    ) {
        SignaturesPropagationData data =
                new SignaturesPropagationData(owner, returnType, receiverType, valueParameters, typeParameters, method);
        return new PropagatedSignature(
                returnType, data.getModifiedReceiverType(), data.getModifiedValueParameters(),
                typeParameters, data.getSignatureErrors(), data.getModifiedHasStableParameterNames()
        );
    }

    @Override
    public void reportSignatureErrors(@NotNull CallableMemberDescriptor descriptor, @NotNull List<String> signatureErrors) {
        trace.record(JvmBindingContextSlices.LOAD_FROM_JAVA_SIGNATURE_ERRORS, descriptor.getOriginal(), signatureErrors);
    }
}
