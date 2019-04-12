/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.lexer.KtTokens;

public class KtConstructorDelegationReferenceExpression extends KtExpressionImpl implements KtReferenceExpression {
    public KtConstructorDelegationReferenceExpression(@NotNull ASTNode node) {
        super(node);
    }

    public boolean isThis() {
        return findChildByType(KtTokens.THIS_KEYWORD) != null;
    }
}
