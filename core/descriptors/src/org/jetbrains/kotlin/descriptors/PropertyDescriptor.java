/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.types.TypeSubstitutor;

import java.util.Collection;
import java.util.List;

public interface PropertyDescriptor extends VariableDescriptorWithAccessors, CallableMemberDescriptor {
    @Override
    @Nullable
    PropertyGetterDescriptor getGetter();

    @Override
    @Nullable
    PropertySetterDescriptor getSetter();

    /**
     * In the following case, the setter is projected out:
     *
     *     trait Tr<T> { var v: T }
     *     fun test(tr: Tr<out String>) {
     *         tr.v = null!! // the assignment is illegal, although a read would be fine
     *     }
     */
    boolean isSetterProjectedOut();

    @NotNull
    List<PropertyAccessorDescriptor> getAccessors();

    @NotNull
    @Override
    PropertyDescriptor getOriginal();

    @NotNull
    @Override
    Collection<? extends PropertyDescriptor> getOverriddenDescriptors();

    @Nullable
    FieldDescriptor getBackingField();

    @Nullable
    FieldDescriptor getDelegateField();

    @Override
    PropertyDescriptor substitute(@NotNull TypeSubstitutor substitutor);

    @NotNull
    @Override
    CopyBuilder<? extends PropertyDescriptor> newCopyBuilder();
}
