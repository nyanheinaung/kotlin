/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface KtDeclarationWithBody extends KtDeclaration {
    @Nullable
    KtExpression getBodyExpression();

    @Nullable
    PsiElement getEqualsToken();

    @Override
    @Nullable
    String getName();

    boolean hasBlockBody();

    boolean hasBody();

    boolean hasDeclaredReturnType();

    @NotNull
    List<KtParameter> getValueParameters();

    @Nullable
    default KtBlockExpression getBodyBlockExpression() {
        KtExpression bodyExpression = getBodyExpression();
        if (bodyExpression instanceof KtBlockExpression) {
            return (KtBlockExpression) bodyExpression;
        }

        return null;
    }
}

