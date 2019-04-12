/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ScriptDescriptor extends ClassDescriptor {
    int getPriority();

    @NotNull
    @Override
    ClassConstructorDescriptor getUnsubstitutedPrimaryConstructor();

    @NotNull
    List<ClassDescriptor> getImplicitReceivers();

    @NotNull
    List<PropertyDescriptor> getScriptProvidedProperties();

    @Nullable
    PropertyDescriptor getResultValue();
}
