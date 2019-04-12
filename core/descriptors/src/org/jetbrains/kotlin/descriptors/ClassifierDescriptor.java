/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.types.SimpleType;
import org.jetbrains.kotlin.types.TypeConstructor;

public interface ClassifierDescriptor extends DeclarationDescriptorNonRoot {
    @NotNull
    TypeConstructor getTypeConstructor();

    @NotNull
    SimpleType getDefaultType();

    @NotNull
    @Override
    ClassifierDescriptor getOriginal();
}
