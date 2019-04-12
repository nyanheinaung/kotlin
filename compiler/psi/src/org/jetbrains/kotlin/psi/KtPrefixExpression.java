/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KtPrefixExpression extends KtUnaryExpression {
    public KtPrefixExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitPrefixExpression(this, data);
    }

    @Override
    @Nullable @IfNotParsed
    public KtExpression getBaseExpression() {
        return PsiTreeUtil.getNextSiblingOfType(getOperationReference(), KtExpression.class);
    }
}
