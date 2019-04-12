/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.SpecialNames;

public class KtFunctionLiteral extends KtFunctionNotStubbed {
    public KtFunctionLiteral(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean hasBlockBody() {
        return false;
    }

    @Override
    public String getName() {
        return SpecialNames.ANONYMOUS;
    }

    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    public boolean hasParameterSpecification() {
        return findChildByType(KtTokens.ARROW) != null;
    }

    @Override
    public KtBlockExpression getBodyExpression() {
        return (KtBlockExpression) super.getBodyExpression();
    }

    @Nullable
    @Override
    public PsiElement getEqualsToken() {
        return null;
    }

    @NotNull
    public PsiElement getLBrace() {
        return findChildByType(KtTokens.LBRACE);
    }

    @Nullable
    @IfNotParsed
    public PsiElement getRBrace() {
        return findChildByType(KtTokens.RBRACE);
    }

    @Nullable
    public PsiElement getArrow() {
        return findChildByType(KtTokens.ARROW);
    }

    @Nullable
    @Override
    public FqName getFqName() {
        return null;
    }

    @Override
    public boolean hasBody() {
        return getBodyExpression() != null;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return new LocalSearchScope(this);
    }
}
