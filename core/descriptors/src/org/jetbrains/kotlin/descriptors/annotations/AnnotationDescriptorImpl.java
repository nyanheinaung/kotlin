/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.SourceElement;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.renderer.DescriptorRenderer;
import org.jetbrains.kotlin.resolve.constants.ConstantValue;
import org.jetbrains.kotlin.types.KotlinType;

import java.util.Map;

public class AnnotationDescriptorImpl implements AnnotationDescriptor {
    private final KotlinType annotationType;
    private final Map<Name, ConstantValue<?>> valueArguments;
    private final SourceElement source;

    public AnnotationDescriptorImpl(
            @NotNull KotlinType annotationType,
            @NotNull Map<Name, ConstantValue<?>> valueArguments,
            @NotNull SourceElement source
    ) {
        this.annotationType = annotationType;
        this.valueArguments = valueArguments;
        this.source = source;
    }

    @Override
    @NotNull
    public KotlinType getType() {
        return annotationType;
    }

    @Nullable
    @Override
    public FqName getFqName() {
        return AnnotationDescriptor.DefaultImpls.getFqName(this);
    }

    @NotNull
    @Override
    public Map<Name, ConstantValue<?>> getAllValueArguments() {
        return valueArguments;
    }

    @Override
    @NotNull
    public SourceElement getSource() {
        return source;
    }

    @Override
    public String toString() {
        return DescriptorRenderer.FQ_NAMES_IN_TYPES.renderAnnotation(this, null);
    }
}
