/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.Collection;
import java.util.List;

public interface CallableDescriptor extends DeclarationDescriptorWithVisibility, DeclarationDescriptorNonRoot,
                                            Substitutable<CallableDescriptor> {
    @Nullable
    ReceiverParameterDescriptor getExtensionReceiverParameter();

    @Nullable
    ReceiverParameterDescriptor getDispatchReceiverParameter();

    @NotNull
    @ReadOnly
    List<TypeParameterDescriptor> getTypeParameters();

    /**
     * Method may return null for not yet fully initialized object or if error occurred.
     */
    @Nullable
    KotlinType getReturnType();

    @NotNull
    @Override
    CallableDescriptor getOriginal();

    @NotNull
    List<ValueParameterDescriptor> getValueParameters();

    /**
     * Kotlin functions always have stable parameter names that can be reliably used when calling them with named arguments.
     * Functions loaded from platform definitions (e.g. Java binaries or JS) may have unstable parameter names that vary from
     * one platform installation to another. These names can not be used reliably for calls with named arguments.
     */
    boolean hasStableParameterNames();

    /**
     * Sometimes parameter names are not available at all (e.g. Java binaries with not enough debug information).
     * In this case, getName() returns synthetic names such as "p0", "p1" etc.
     */
    boolean hasSynthesizedParameterNames();

    @NotNull
    Collection<? extends CallableDescriptor> getOverriddenDescriptors();

    interface UserDataKey<V> {}

    // TODO: pull up userdata related members to DeclarationDescriptor and use more efficient implementation (e.g. THashMap)
    @Nullable
    <V> V getUserData(UserDataKey<V> key);
}
