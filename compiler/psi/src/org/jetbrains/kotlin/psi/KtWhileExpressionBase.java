/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;

public abstract class KtWhileExpressionBase extends KtLoopExpression {
    public KtWhileExpressionBase(@NotNull ASTNode node) {
        super(node);
    }

    @Nullable
    @IfNotParsed
    public KtExpression getCondition() {
        return findExpressionUnder(KtNodeTypes.CONDITION);
    }
}
