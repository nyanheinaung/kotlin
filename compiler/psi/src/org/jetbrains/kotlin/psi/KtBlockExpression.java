/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifiableCodeBlock;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;

import java.util.Arrays;
import java.util.List;

public class KtBlockExpression extends KtExpressionImpl implements KtStatementExpression, PsiModifiableCodeBlock {
    public KtBlockExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean shouldChangeModificationCount(PsiElement place) {
        // To prevent OutOfBlockModification increase from JavaCodeBlockModificationListener
        return false;
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitBlockExpression(this, data);
    }

    @ReadOnly
    @NotNull
    public List<KtExpression> getStatements() {
        return Arrays.asList(findChildrenByClass(KtExpression.class));
    }

    @Nullable
    public TextRange getLastBracketRange() {
        PsiElement rBrace = getRBrace();
        return rBrace != null ? rBrace.getTextRange() : null;
    }

    @Nullable
    public PsiElement getRBrace() {
        return findChildByType(KtTokens.RBRACE);
    }

    @Nullable
    public PsiElement getLBrace() {
        return findChildByType(KtTokens.LBRACE);
    }
}
