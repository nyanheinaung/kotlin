/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.SourceElement;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.storage.StorageManager;

public abstract class ClassDescriptorBase extends AbstractClassDescriptor {

    private final DeclarationDescriptor containingDeclaration;
    private final SourceElement source;
    private final boolean isExternal;

    protected ClassDescriptorBase(
            @NotNull StorageManager storageManager,
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull Name name,
            @NotNull SourceElement source,
            boolean isExternal
    ) {
        super(storageManager, name);
        this.containingDeclaration = containingDeclaration;
        this.source = source;
        this.isExternal = isExternal;
    }

    @Override
    public boolean isExternal() {
        return isExternal;
    }

    @NotNull
    @Override
    public DeclarationDescriptor getContainingDeclaration() {
        return containingDeclaration;
    }

    @NotNull
    @Override
    public SourceElement getSource() {
        return source;
    }
}
