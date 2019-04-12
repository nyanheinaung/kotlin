/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors;

import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.resolve.scopes.LexicalScope;

import java.util.Collection;

public interface ClassDescriptorWithResolutionScopes extends ClassDescriptor {
    @NotNull
    LexicalScope getScopeForClassHeaderResolution();

    @NotNull
    LexicalScope getScopeForConstructorHeaderResolution();

    @NotNull
    LexicalScope getScopeForCompanionObjectHeaderResolution();

    @NotNull
    LexicalScope getScopeForMemberDeclarationResolution();

    @NotNull
    LexicalScope getScopeForStaticMemberDeclarationResolution();

    @NotNull
    LexicalScope getScopeForInitializerResolution();

    @Nullable
    @Override
    ClassDescriptorWithResolutionScopes getCompanionObjectDescriptor();

    @NotNull
    @ReadOnly
    Collection<CallableMemberDescriptor> getDeclaredCallableMembers();
}
