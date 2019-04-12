/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorNonRoot;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithSource;
import org.jetbrains.kotlin.descriptors.SourceElement;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.name.Name;

public abstract class DeclarationDescriptorNonRootImpl
        extends DeclarationDescriptorImpl
        implements DeclarationDescriptorNonRoot {

    @NotNull
    private final DeclarationDescriptor containingDeclaration;

    @NotNull
    private final SourceElement source;

    protected DeclarationDescriptorNonRootImpl(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Annotations annotations,
            @NotNull Name name,
            @NotNull SourceElement source
    ) {
        super(annotations, name);

        this.containingDeclaration = containingDeclaration;
        this.source = source;
    }

    @NotNull
    @Override
    public DeclarationDescriptorWithSource getOriginal() {
        return (DeclarationDescriptorWithSource) super.getOriginal();
    }

    @Override
    @NotNull
    public DeclarationDescriptor getContainingDeclaration() {
        return containingDeclaration;
    }

    @Override
    @NotNull
    public SourceElement getSource() {
        return source;
    }

}
