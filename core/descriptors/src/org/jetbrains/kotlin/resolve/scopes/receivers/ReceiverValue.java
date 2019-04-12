/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes.receivers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.types.KotlinType;

public interface ReceiverValue extends Receiver {

    @NotNull
    KotlinType getType();

    @NotNull
    ReceiverValue replaceType(@NotNull KotlinType newType);

    @NotNull
    ReceiverValue getOriginal();
}
