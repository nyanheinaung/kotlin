/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.name.Name;

import java.util.List;

/**
 * Simple functions are the ones with 'fun' keyword and function literals
 */
public interface SimpleFunctionDescriptor extends FunctionDescriptor {
    @NotNull
    @Override
    SimpleFunctionDescriptor copy(DeclarationDescriptor newOwner, Modality modality, Visibility visibility, Kind kind, boolean copyOverrides);

    @NotNull
    @Override
    SimpleFunctionDescriptor getOriginal();

    @NotNull
    @Override
    CopyBuilder<? extends SimpleFunctionDescriptor> newCopyBuilder();
}
