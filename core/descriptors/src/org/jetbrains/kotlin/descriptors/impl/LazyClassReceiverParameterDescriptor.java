/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor;
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor;
import org.jetbrains.kotlin.descriptors.annotations.Annotations;
import org.jetbrains.kotlin.resolve.scopes.receivers.ImplicitClassReceiver;
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue;

public class LazyClassReceiverParameterDescriptor extends AbstractReceiverParameterDescriptor {
    private final ClassDescriptor descriptor;
    private final ImplicitClassReceiver receiverValue;

    public LazyClassReceiverParameterDescriptor(@NotNull ClassDescriptor descriptor) {
        super(Annotations.Companion.getEMPTY());
        this.descriptor = descriptor;
        this.receiverValue = new ImplicitClassReceiver(descriptor, null);
    }

    @NotNull
    @Override
    public ReceiverValue getValue() {
        return receiverValue;
    }

    @NotNull
    @Override
    public DeclarationDescriptor getContainingDeclaration() {
        return descriptor;
    }

    @NotNull
    @Override
    public ReceiverParameterDescriptor copy(@NotNull DeclarationDescriptor newOwner) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return "class " + descriptor.getName() + "::this";
    }
}
