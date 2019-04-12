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

public class KtWhenConditionInRange extends KtWhenCondition {
    public KtWhenConditionInRange(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isNegated() {
        return getNode().findChildByType(KtTokens.NOT_IN) != null;
    }

    @Nullable @IfNotParsed
    public KtExpression getRangeExpression() {
        // Copied from JetBinaryExpression
        ASTNode node = getOperationReference().getNode().getTreeNext();
        while (node != null) {
            PsiElement psi = node.getPsi();
            if (psi instanceof KtExpression) {
                return (KtExpression) psi;
            }
            node = node.getTreeNext();
        }

        return null;
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitWhenConditionInRange(this, data);
    }

    @NotNull
    public KtOperationReferenceExpression getOperationReference() {
        return (KtOperationReferenceExpression) findChildByType(KtNodeTypes.OPERATION_REFERENCE);
    }
}
