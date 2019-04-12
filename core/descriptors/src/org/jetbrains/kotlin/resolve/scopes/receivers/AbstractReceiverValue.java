/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes.receivers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.types.KotlinType;

public abstract class AbstractReceiverValue implements ReceiverValue {
    protected final KotlinType receiverType;
    private final ReceiverValue original;

    public AbstractReceiverValue(@NotNull KotlinType receiverType, @Nullable ReceiverValue original) {
        this.receiverType = receiverType;
        this.original = original != null ? original : this;
    }

    @Override
    @NotNull
    public KotlinType getType() {
        return receiverType;
    }

    @NotNull
    @Override
    public ReceiverValue getOriginal() {
        return original;
    }
}
