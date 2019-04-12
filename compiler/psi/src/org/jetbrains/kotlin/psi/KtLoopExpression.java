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

public abstract class KtLoopExpression extends KtExpressionImpl implements KtStatementExpression {
    public KtLoopExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public KtExpression getBody() {
        return findExpressionUnder(KtNodeTypes.BODY);
    }

    @Nullable
    @IfNotParsed
    public PsiElement getLeftParenthesis() {
        return findChildByType(KtTokens.LPAR);
    }

    @Nullable @IfNotParsed
    public PsiElement getRightParenthesis() {
        return findChildByType(KtTokens.RPAR);
    }
}
