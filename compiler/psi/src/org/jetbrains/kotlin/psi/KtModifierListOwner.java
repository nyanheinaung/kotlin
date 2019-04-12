/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken;

public interface KtModifierListOwner extends PsiElement, KtAnnotated {
    @Nullable
    KtModifierList getModifierList();

    boolean hasModifier(@NotNull KtModifierKeywordToken modifier);

    void addModifier(@NotNull KtModifierKeywordToken modifier);
    void removeModifier(@NotNull KtModifierKeywordToken modifier);

    @NotNull
    KtAnnotationEntry addAnnotationEntry(@NotNull KtAnnotationEntry annotationEntry);
}
