/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.types.KotlinType;
import org.jetbrains.kotlin.types.TypeSubstitutor;

import java.util.List;

public interface ConstructorDescriptor extends FunctionDescriptor {
    @NotNull
    @Override
    List<TypeParameterDescriptor> getTypeParameters();

    @NotNull
    @Override
    KotlinType getReturnType();

    @NotNull
    @Override
    ClassifierDescriptorWithTypeParameters getContainingDeclaration();

    @NotNull
    ClassDescriptor getConstructedClass();

    @NotNull
    @Override
    ConstructorDescriptor getOriginal();

    @Nullable
    @Override
    ConstructorDescriptor substitute(@NotNull TypeSubstitutor substitutor);

    /**
     * @return "&lt;init&gt;" -- name is not stored for constructors
     */
    @NotNull
    @Override
    Name getName();

    @NotNull
    @Override
    ConstructorDescriptor copy(
            DeclarationDescriptor newOwner,
            Modality modality,
            Visibility visibility,
            Kind kind,
            boolean copyOverrides
    );

    boolean isPrimary();
}
