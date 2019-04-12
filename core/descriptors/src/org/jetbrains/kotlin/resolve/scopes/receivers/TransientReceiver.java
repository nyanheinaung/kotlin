/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes.receivers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.types.KotlinType;

/**
 * This represents the receiver of hasNext and next() in for-loops
 * Cannot be an expression receiver because there is no expression for the iterator() call
 */
public class TransientReceiver extends AbstractReceiverValue {
    public TransientReceiver(@NotNull KotlinType type) {
        this(type, null);
    }

    private TransientReceiver(@NotNull KotlinType type, @Nullable ReceiverValue original) {
        super(type, original);
    }

    @Override
    public String toString() {
        return "{Transient} : " + getType();
    }

    @NotNull
    @Override
    public ReceiverValue replaceType(@NotNull KotlinType newType) {
        return new TransientReceiver(newType, getOriginal());
    }
}
