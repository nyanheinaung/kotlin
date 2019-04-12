/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;
import org.jetbrains.kotlin.lexer.KtTokens;

public class KtIsExpression extends KtExpressionImpl implements KtOperationExpression {
    public KtIsExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitIsExpression(this, data);
    }

    @NotNull
    public KtExpression getLeftHandSide() {
        return findChildByClass(KtExpression.class);
    }

    @Nullable @IfNotParsed
    public KtTypeReference getTypeReference() {
        return (KtTypeReference) findChildByType(KtNodeTypes.TYPE_REFERENCE);
    }

    @Override
    @NotNull
    public KtSimpleNameExpression getOperationReference() {
        return (KtSimpleNameExpression) findChildByType(KtNodeTypes.OPERATION_REFERENCE);
    }

    public boolean isNegated() {
        return getOperationReference().getReferencedNameElementType() == KtTokens.NOT_IS;
    }

}
