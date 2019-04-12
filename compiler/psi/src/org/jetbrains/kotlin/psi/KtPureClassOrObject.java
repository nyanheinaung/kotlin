/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A minimal interface that {@link KtClassOrObject} implements for the purpose of code-generation that does not need the full power of PSI.
 * This interface can be easily implemented by synthetic elements to generate code for them.
 */
public interface KtPureClassOrObject extends KtPureElement, KtDeclarationContainer {
    @Nullable
    String getName();

    boolean isLocal();

    @NotNull
    @ReadOnly
    List<KtSuperTypeListEntry> getSuperTypeListEntries();

    @NotNull
    @ReadOnly
    List<KtObjectDeclaration> getCompanionObjects();

    boolean hasExplicitPrimaryConstructor();

    boolean hasPrimaryConstructor();

    @Nullable
    KtPrimaryConstructor getPrimaryConstructor();

    @Nullable
    KtModifierList getPrimaryConstructorModifierList();

    @NotNull
    @ReadOnly
    List<KtParameter> getPrimaryConstructorParameters();

    @NotNull
    @ReadOnly
    List<KtSecondaryConstructor> getSecondaryConstructors();

    @Nullable
    KtClassBody getBody();
}

