/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KtCallableDeclaration extends KtNamedDeclaration, KtTypeParameterListOwner {
    @Nullable
    KtParameterList getValueParameterList();

    @NotNull
    List<KtParameter> getValueParameters();

    @Nullable
    KtTypeReference getReceiverTypeReference();

    @Nullable
    KtTypeReference getTypeReference();

    @Nullable
    KtTypeReference setTypeReference(@Nullable KtTypeReference typeRef);

    @Nullable
    PsiElement getColon();
}
