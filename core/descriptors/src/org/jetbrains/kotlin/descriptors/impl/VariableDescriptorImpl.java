/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.*;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class VariableDescriptorImpl extends DeclarationDescriptorNonRootImpl implements VariableDescriptor {
    protected KotlinType outType;

    public VariableDescriptorImpl(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            @NotNull Name name,
            @Nullable KotlinType outType,
            @NotNull SourceElement source
    ) {
        super(containingDeclaration, annotations, name, source);

        this.outType = outType;
    }

    @NotNull
    @Override
    public KotlinType getType() {
        return outType;
    }

    public void setOutType(KotlinType outType) {
        assert this.outType == null;
        this.outType = outType;
    }

    @Override
    @NotNull
    public VariableDescriptor getOriginal() {
        return (VariableDescriptor) super.getOriginal();
    }

    @NotNull
    @Override
    public List<ValueParameterDescriptor> getValueParameters() {
        return Collections.emptyList();
    }

    @Override
    public boolean hasStableParameterNames() {
        return false;
    }

    @Override
    public boolean hasSynthesizedParameterNames() {
        return false;
    }

    @NotNull
    @Override
    public Collection<? extends CallableDescriptor> getOverriddenDescriptors() {
        return Collections.emptySet();
    }

    @NotNull
    @Override
    public List<TypeParameterDescriptor> getTypeParameters() {
        return Collections.emptyList();
    }

    @Override
    public ReceiverParameterDescriptor getExtensionReceiverParameter() {
        return null;
    }

    @Override
    public ReceiverParameterDescriptor getDispatchReceiverParameter() {
        return null;
    }

    @NotNull
    @Override
    public KotlinType getReturnType() {
        return getType();
    }

    @Override
    public boolean isConst() {
        return false;
    }

    @Nullable
    @Override
    public <V> V getUserData(UserDataKey<V> key) {
        return null;
    }
}
