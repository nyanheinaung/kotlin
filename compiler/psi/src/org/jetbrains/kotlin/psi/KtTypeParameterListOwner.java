/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KtTypeParameterListOwner extends KtNamedDeclaration {
    @Nullable
    KtTypeParameterList getTypeParameterList();

    @Nullable
    KtTypeConstraintList getTypeConstraintList();

    @NotNull
    List<KtTypeConstraint> getTypeConstraints();

    @NotNull
    List<KtTypeParameter> getTypeParameters();
}
