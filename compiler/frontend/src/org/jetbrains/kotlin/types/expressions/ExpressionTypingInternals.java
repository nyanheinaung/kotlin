/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.*;

/*package*/ interface ExpressionTypingInternals extends ExpressionTypingFacade {
    @NotNull
    KotlinTypeInfo checkInExpression(
            @NotNull KtElement callElement,
            @NotNull KtSimpleNameExpression operationSign,
            @NotNull ValueArgument leftArgument,
            @Nullable KtExpression right,
            @NotNull ExpressionTypingContext context
    );

    void checkStatementType(@NotNull KtExpression expression, ExpressionTypingContext context);

    @NotNull
    ExpressionTypingComponents getComponents();
}
