/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;
import org.jetbrains.kotlin.lexer.KtTokens;

public class KtForExpression extends KtLoopExpression {
    public KtForExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitForExpression(this, data);
    }

    @Nullable @IfNotParsed
    public KtParameter getLoopParameter() {
        return (KtParameter) findChildByType(KtNodeTypes.VALUE_PARAMETER);
    }

    @Nullable
    public KtDestructuringDeclaration getDestructuringDeclaration() {
        KtParameter loopParameter = getLoopParameter();
        if (loopParameter == null) return null;
        return loopParameter.getDestructuringDeclaration();
    }

    @Nullable @IfNotParsed
    public KtExpression getLoopRange() {
        return findExpressionUnder(KtNodeTypes.LOOP_RANGE);
    }

    @Nullable @IfNotParsed
    public PsiElement getInKeyword() {
        return findChildByType(KtTokens.IN_KEYWORD);
    }

    @NotNull
    public PsiElement getForKeyword() {
        return findChildByType(KtTokens.FOR_KEYWORD);
    }
}
