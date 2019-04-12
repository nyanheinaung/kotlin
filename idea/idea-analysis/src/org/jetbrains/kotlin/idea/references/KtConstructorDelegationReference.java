/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.references;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.KtConstructorDelegationReferenceExpression;

import java.util.Collection;
import java.util.Collections;

public class KtConstructorDelegationReference extends KtSimpleReference<KtConstructorDelegationReferenceExpression> {
    public KtConstructorDelegationReference(KtConstructorDelegationReferenceExpression expression) {
        super(expression);
    }

    @Override
    public TextRange getRangeInElement() {
        return new TextRange(0, getElement().getTextLength());
    }

    @NotNull
    @Override
    public Collection<Name> getResolvesByNames() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public PsiElement handleElementRename(@Nullable String newElementName) {
        // Class rename never affects this reference, so there is no need to fail with exception
        return getExpression();
    }
}
