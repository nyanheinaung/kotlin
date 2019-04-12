/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.KtNodeTypes;

public class KtFinallySection extends KtElementImpl implements KtStatementExpression {
    public KtFinallySection(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitFinallySection(this, data);
    }

    public KtBlockExpression getFinalExpression() {
        return (KtBlockExpression) findChildByType(KtNodeTypes.BLOCK);
    }
}
