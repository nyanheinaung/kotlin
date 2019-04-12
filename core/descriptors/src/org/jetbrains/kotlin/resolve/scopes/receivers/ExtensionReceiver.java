/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes.receivers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.CallableDescriptor;
import org.jetbrains.kotlin.types.KotlinType;

public class ExtensionReceiver extends AbstractReceiverValue implements ImplicitReceiver {

    private final CallableDescriptor descriptor;

    public ExtensionReceiver(
            @NotNull CallableDescriptor callableDescriptor,
            @NotNull KotlinType receiverType,
            @Nullable ReceiverValue original
    ) {
        super(receiverType, original);
        this.descriptor = callableDescriptor;
    }

    @NotNull
    @Override
    public CallableDescriptor getDeclarationDescriptor() {
        return descriptor;
    }

    @NotNull
    @Override
    public ReceiverValue replaceType(@NotNull KotlinType newType) {
        return new ExtensionReceiver(descriptor, newType, getOriginal());
    }

    @Override
    public String toString() {
        return getType() + ": Ext {" + descriptor + "}";
    }
}
