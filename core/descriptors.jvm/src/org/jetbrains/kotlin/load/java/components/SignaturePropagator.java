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
import org.jetbrains.kotlin.types.KotlinType;

import java.util.Collections;
import java.util.List;

public interface SignaturePropagator {
    SignaturePropagator DO_NOTHING = new SignaturePropagator() {
        @NotNull
        @Override
        public PropagatedSignature resolvePropagatedSignature(
                @NotNull JavaMethod method,
                @NotNull ClassDescriptor owner,
                @NotNull KotlinType returnType,
                @Nullable KotlinType receiverType,
                @NotNull List<ValueParameterDescriptor> valueParameters,
                @NotNull List<TypeParameterDescriptor> typeParameters
        ) {
            return new PropagatedSignature(
                    returnType, receiverType, valueParameters, typeParameters, Collections.<String>emptyList(), false
            );
        }

        @Override
        public void reportSignatureErrors(@NotNull CallableMemberDescriptor descriptor, @NotNull List<String> signatureErrors) {
            throw new UnsupportedOperationException("Should not be called");
        }
    };

    class PropagatedSignature {
        private final KotlinType returnType;
        private final KotlinType receiverType;
        private final List<ValueParameterDescriptor> valueParameters;
        private final List<TypeParameterDescriptor> typeParameters;
        private final List<String> signatureErrors;
        private final boolean hasStableParameterNames;

        public PropagatedSignature(
                @NotNull KotlinType returnType,
                @Nullable KotlinType receiverType,
                @NotNull List<ValueParameterDescriptor> valueParameters,
                @NotNull List<TypeParameterDescriptor> typeParameters,
                @NotNull List<String> signatureErrors,
                boolean hasStableParameterNames
        ) {
            this.returnType = returnType;
            this.receiverType = receiverType;
            this.valueParameters = valueParameters;
            this.typeParameters = typeParameters;
            this.signatureErrors = signatureErrors;
            this.hasStableParameterNames = hasStableParameterNames;
        }

        @NotNull
        public KotlinType getReturnType() {
            return returnType;
        }

        @Nullable
        public KotlinType getReceiverType() {
            return receiverType;
        }

        @NotNull
        public List<ValueParameterDescriptor> getValueParameters() {
            return valueParameters;
        }

        @NotNull
        public List<TypeParameterDescriptor> getTypeParameters() {
            return typeParameters;
        }

        public boolean hasStableParameterNames() {
            return hasStableParameterNames;
        }

        @NotNull
        public List<String> getErrors() {
            return signatureErrors;
        }
    }

    @NotNull
    PropagatedSignature resolvePropagatedSignature(
            @NotNull JavaMethod method,
            @NotNull ClassDescriptor owner,
            @NotNull KotlinType returnType,
            @Nullable KotlinType receiverType,
            @NotNull List<ValueParameterDescriptor> valueParameters,
            @NotNull List<TypeParameterDescriptor> typeParameters
    );

    void reportSignatureErrors(@NotNull CallableMemberDescriptor descriptor, @NotNull List<String> signatureErrors);
}
