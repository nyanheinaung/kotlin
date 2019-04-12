/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;

public class KtWhenEntry extends KtElementImpl {
    public KtWhenEntry(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isElse() {
        return getElseKeyword() != null;
    }

    @Nullable
    public PsiElement getElseKeyword() {
        return findChildByType(KtTokens.ELSE_KEYWORD);
    }

    @Nullable
    public KtExpression getExpression() {
        return findChildByClass(KtExpression.class);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitWhenEntry(this, data);
    }

    @NotNull
    public KtWhenCondition[] getConditions() {
        return findChildrenByClass(KtWhenCondition.class);
    }
}
