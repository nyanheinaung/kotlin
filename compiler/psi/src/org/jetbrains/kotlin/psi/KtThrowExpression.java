/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KtThrowExpression extends KtExpressionImpl implements KtStatementExpression {
    public KtThrowExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitThrowExpression(this, data);
    }

    @Nullable @IfNotParsed
    public KtExpression getThrownExpression() {
        return findChildByClass(KtExpression.class);
    }
}
