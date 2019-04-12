/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.codegen.OwnerKind;
import org.jetbrains.kotlin.codegen.state.KotlinTypeMapper;
import org.jetbrains.kotlin.descriptors.ClassDescriptor;

public class AnonymousClassContext extends ClassContext {

    public AnonymousClassContext(
            @NotNull KotlinTypeMapper typeMapper,
            @NotNull ClassDescriptor contextDescriptor,
            @NotNull OwnerKind contextKind,
            @Nullable CodegenContext parentContext,
            @Nullable LocalLookup localLookup
    ) {
        super(typeMapper, contextDescriptor, contextKind, parentContext, localLookup);
    }

    @Override
    public String toString() {
        return "Anonymous: " + getThisDescriptor();
    }
}
