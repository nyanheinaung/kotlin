/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.translate.intrinsic.operation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.js.backend.ast.JsExpression;
import org.jetbrains.kotlin.js.translate.context.TranslationContext;
import org.jetbrains.kotlin.psi.KtBinaryExpression;

@FunctionalInterface
public interface BinaryOperationIntrinsic {
    @NotNull
    JsExpression invoke(
            @NotNull KtBinaryExpression expression,
            @NotNull JsExpression left,
            @NotNull JsExpression right,
            @NotNull TranslationContext context
    );
}
