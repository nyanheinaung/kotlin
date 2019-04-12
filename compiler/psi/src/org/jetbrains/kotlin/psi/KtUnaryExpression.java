/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;

public abstract class KtUnaryExpression extends KtExpressionImpl implements KtOperationExpression {
    public KtUnaryExpression(ASTNode node) {
        super(node);
    }

    @Nullable @IfNotParsed
    public abstract KtExpression getBaseExpression();

    @Override
    @NotNull
    public KtSimpleNameExpression getOperationReference() {
        return (KtSimpleNameExpression) findChildByType(KtNodeTypes.OPERATION_REFERENCE);
    }

    public IElementType getOperationToken() {
        return getOperationReference().getReferencedNameElementType();
    }
}
