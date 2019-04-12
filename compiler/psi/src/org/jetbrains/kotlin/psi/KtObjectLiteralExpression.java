/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.KtNodeTypes;

public class KtObjectLiteralExpression extends KtExpressionImpl {
    public KtObjectLiteralExpression(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitObjectLiteralExpression(this, data);
    }

    @NotNull
    public KtObjectDeclaration getObjectDeclaration() {
        return (KtObjectDeclaration) findChildByType(KtNodeTypes.OBJECT_DECLARATION);
    }
}
