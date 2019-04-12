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

public class KtDoWhileExpression extends KtWhileExpressionBase {
    public KtDoWhileExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitDoWhileExpression(this, data);
    }

    @Nullable
    @IfNotParsed
    public PsiElement getWhileKeyword() {
        //noinspection ConstantConditions
        return findChildByType(KtTokens.WHILE_KEYWORD);
    }
}
