/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface PropertyAccessorDescriptor extends VariableAccessorDescriptor {
    boolean isDefault();

    @NotNull
    @Override
    PropertyAccessorDescriptor getOriginal();

    @Override
    @NotNull
    Collection<? extends PropertyAccessorDescriptor> getOverriddenDescriptors();

    @NotNull
    PropertyDescriptor getCorrespondingProperty();

    @NotNull
    @Override
    PropertyAccessorDescriptor copy(
            DeclarationDescriptor newOwner,
            Modality modality,
            Visibility visibility,
            Kind kind,
            boolean copyOverrides
    );
}
