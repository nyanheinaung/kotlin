/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue;

public class ReceiverParameterDescriptorImpl extends AbstractReceiverParameterDescriptor {
    private final DeclarationDescriptor containingDeclaration;
    private final ReceiverValue value;

    public ReceiverParameterDescriptorImpl(
            @NotNull DeclarationDescriptor containingDeclaration,
            @NotNull ReceiverValue value,
            @NotNull Annotations annotations
    ) {
        super(annotations);
        this.containingDeclaration = containingDeclaration;
        this.value = value;
    }

    @NotNull
    @Override
    public ReceiverValue getValue() {
        return value;
    }

    @NotNull
    @Override
    public DeclarationDescriptor getContainingDeclaration() {
        return containingDeclaration;
    }

    @NotNull
    @Override
    public ReceiverParameterDescriptor copy(@NotNull DeclarationDescriptor newOwner) {
        return new ReceiverParameterDescriptorImpl(newOwner, value, getAnnotations());
    }
}
