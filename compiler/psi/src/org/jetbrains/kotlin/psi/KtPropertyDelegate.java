/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;

public class KtPropertyDelegate extends KtElementImpl {
    public KtPropertyDelegate(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    public KtExpression getExpression() {
        return findChildByClass(KtExpression.class);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitPropertyDelegate(this, data);
    }

    @NotNull
    public ASTNode getByKeywordNode() {
        //noinspection ConstantConditions
        return getNode().findChildByType(KtTokens.BY_KEYWORD);
    }
}
