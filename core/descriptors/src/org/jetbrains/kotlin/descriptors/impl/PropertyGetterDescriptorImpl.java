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

public class PropertyGetterDescriptorImpl extends PropertyAccessorDescriptorImpl implements PropertyGetterDescriptor {
    private KotlinType returnType;

    @NotNull
    private final PropertyGetterDescriptor original;

    public PropertyGetterDescriptorImpl(
            @NotNull PropertyDescriptor correspondingProperty,
            @NotNull Annotations annotations,
            @NotNull Modality modality,
            @NotNull Visibility visibility,
            boolean isDefault,
            boolean isExternal,
            boolean isInline,
            @NotNull Kind kind,
            @Nullable PropertyGetterDescriptor original,
            @NotNull SourceElement source
    ) {
        super(modality, visibility, correspondingProperty, annotations, Name.special("<get-" + correspondingProperty.getName() + ">"),
              isDefault, isExternal, isInline, kind, source);
        this.original = original != null ? original : this;
    }
    
    public void initialize(KotlinType returnType) {
        this.returnType = returnType == null ? getCorrespondingProperty().getType() : returnType;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Collection<? extends PropertyGetterDescriptor> getOverriddenDescriptors() {
        return (Collection) super.getOverriddenDescriptors(true);
    }

    @NotNull
    @Override
    public List<ValueParameterDescriptor> getValueParameters() {
        return Collections.emptyList();
    }

    @Override
    public KotlinType getReturnType() {
        return returnType;
    }

    @Override
    public <R, D> R accept(DeclarationDescriptorVisitor<R, D> visitor, D data) {
        return visitor.visitPropertyGetterDescriptor(this, data);
    }

    @NotNull
    @Override
    public PropertyGetterDescriptor getOriginal() {
        return this.original;
    }
}
