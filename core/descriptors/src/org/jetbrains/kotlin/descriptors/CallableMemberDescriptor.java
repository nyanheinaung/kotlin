/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.types.TypeSubstitution;

import java.util.Collection;
import java.util.List;

public interface CallableMemberDescriptor extends CallableDescriptor, MemberDescriptor {
    @NotNull
    @Override
    Collection<? extends CallableMemberDescriptor> getOverriddenDescriptors();

    @NotNull
    @Override
    CallableMemberDescriptor getOriginal();

    void setOverriddenDescriptors(@NotNull Collection<? extends CallableMemberDescriptor> overriddenDescriptors);

    enum Kind {
        DECLARATION,
        FAKE_OVERRIDE,
        DELEGATION,
        SYNTHESIZED
        ;
        
        public boolean isReal() {
            return this != FAKE_OVERRIDE;
        }
    }

    /**
     * Is this a real function or function projection.
     */
    @NotNull
    Kind getKind();

    @NotNull
    CallableMemberDescriptor copy(DeclarationDescriptor newOwner, Modality modality, Visibility visibility, Kind kind, boolean copyOverrides);

    @NotNull
    CopyBuilder<? extends CallableMemberDescriptor> newCopyBuilder();

    interface CopyBuilder<D extends CallableMemberDescriptor> {
        @NotNull
        CopyBuilder<D> setOwner(@NotNull DeclarationDescriptor owner);

        @NotNull
        CopyBuilder<D> setModality(@NotNull Modality modality);

        @NotNull
        CopyBuilder<D> setVisibility(@NotNull Visibility visibility);

        @NotNull
        CopyBuilder<D> setKind(@NotNull Kind kind);

        @NotNull
        CopyBuilder<D> setTypeParameters(@NotNull List<TypeParameterDescriptor> parameters);

        @NotNull
        CopyBuilder<D> setDispatchReceiverParameter(@Nullable ReceiverParameterDescriptor dispatchReceiverParameter);

        @NotNull
        CopyBuilder<D> setSubstitution(@NotNull TypeSubstitution substitution);

        @NotNull
        CopyBuilder<D> setCopyOverrides(boolean copyOverrides);

        @NotNull
        CopyBuilder<D> setName(@NotNull Name name);

        @NotNull
        CopyBuilder<D> setOriginal(@Nullable CallableMemberDescriptor original);

        @Nullable
        D build();
    }
}
