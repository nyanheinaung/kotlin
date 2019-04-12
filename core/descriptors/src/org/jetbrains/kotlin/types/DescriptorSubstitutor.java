/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types;

import kotlin.annotations.jvm.Mutable;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.SourceElement;
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor;
import org.jetbrains.kotlin.descriptors.impl.TypeParameterDescriptorImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DescriptorSubstitutor {
    private DescriptorSubstitutor() {
    }

    @NotNull
    public static TypeSubstitutor substituteTypeParameters(
            @ReadOnly @NotNull List<TypeParameterDescriptor> typeParameters,
            @NotNull TypeSubstitution originalSubstitution,
            @NotNull DeclarationDescriptor newContainingDeclaration,
            @NotNull @Mutable List<TypeParameterDescriptor> result
    ) {
        TypeSubstitutor substitutor = substituteTypeParameters(typeParameters, originalSubstitution, newContainingDeclaration, result, null);
        if (substitutor == null) throw new AssertionError("Substitution failed");
        return substitutor;
    }

    @Nullable
    public static TypeSubstitutor substituteTypeParameters(
            @ReadOnly @NotNull List<TypeParameterDescriptor> typeParameters,
            @NotNull TypeSubstitution originalSubstitution,
            @NotNull DeclarationDescriptor newContainingDeclaration,
            @NotNull @Mutable List<TypeParameterDescriptor> result,
            @Nullable boolean[] wereChanges
    ) {
        Map<TypeConstructor, TypeProjection> mutableSubstitution = new HashMap<TypeConstructor, TypeProjection>();

        Map<TypeParameterDescriptor, TypeParameterDescriptorImpl> substitutedMap = new HashMap<TypeParameterDescriptor, TypeParameterDescriptorImpl>();
        int index = 0;
        for (TypeParameterDescriptor descriptor : typeParameters) {
            TypeParameterDescriptorImpl substituted = TypeParameterDescriptorImpl.createForFurtherModification(
                    newContainingDeclaration,
                    descriptor.getAnnotations(),
                    descriptor.isReified(),
                    descriptor.getVariance(),
                    descriptor.getName(),
                    index++,
                    SourceElement.NO_SOURCE
            );

            mutableSubstitution.put(descriptor.getTypeConstructor(), new TypeProjectionImpl(substituted.getDefaultType()));

            substitutedMap.put(descriptor, substituted);
            result.add(substituted);
        }

        TypeSubstitutor substitutor = TypeSubstitutor.createChainedSubstitutor(
                originalSubstitution, TypeConstructorSubstitution.createByConstructorsMap(mutableSubstitution)
        );

        for (TypeParameterDescriptor descriptor : typeParameters) {
            TypeParameterDescriptorImpl substituted = substitutedMap.get(descriptor);
            for (KotlinType upperBound : descriptor.getUpperBounds()) {
                KotlinType substitutedBound = substitutor.substitute(upperBound, Variance.IN_VARIANCE);
                if (substitutedBound == null) return null;

                if (substitutedBound != upperBound && wereChanges != null) {
                    wereChanges[0] = true;
                }

                substituted.addUpperBound(substitutedBound);
            }
            substituted.setInitialized();
        }

        return substitutor;
    }
}
