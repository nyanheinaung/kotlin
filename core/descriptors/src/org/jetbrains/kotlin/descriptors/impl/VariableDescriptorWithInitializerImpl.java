/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.SourceElement;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.resolve.constants.ConstantValue;
import org.jetbrains.kotlin.storage.NullableLazyValue;
import org.jetbrains.kotlin.types.KotlinType;

public abstract class VariableDescriptorWithInitializerImpl extends VariableDescriptorImpl {
    private final boolean isVar;

    protected NullableLazyValue<ConstantValue<?>> compileTimeInitializer;

    public VariableDescriptorWithInitializerImpl(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            @NotNull Name name,
            @Nullable KotlinType outType,
            boolean isVar,
            @NotNull SourceElement source
    ) {
        super(containingDeclaration, annotations, name, outType, source);

        this.isVar = isVar;
    }

    @Override
    public boolean isVar() {
        return isVar;
    }

    @Nullable
    @Override
    public ConstantValue<?> getCompileTimeInitializer() {
        if (compileTimeInitializer != null) {
            return compileTimeInitializer.invoke();
        }
        return null;
    }

    public void setCompileTimeInitializer(@NotNull NullableLazyValue<ConstantValue<?>> compileTimeInitializer) {
        assert !isVar() : "Constant value for variable initializer should be recorded only for final variables: " + getName();
        this.compileTimeInitializer = compileTimeInitializer;
    }
}
