/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.psi.ValueArgument;

import java.util.Collections;
import java.util.List;

public class ExpressionValueArgument implements ResolvedValueArgument {
    private final ValueArgument valueArgument;

    public ExpressionValueArgument(@Nullable ValueArgument valueArgument) {
        this.valueArgument = valueArgument;
    }

    // Nullable when something like f(a, , b) was in the source code
    @Nullable
    public ValueArgument getValueArgument() {
        return valueArgument;
    }

    @NotNull
    @Override
    public List<ValueArgument> getArguments() {
        if (valueArgument == null) return Collections.emptyList();
        return Collections.singletonList(valueArgument);
    }

    @Override
    public String toString() {
        KtExpression expression = valueArgument.getArgumentExpression();
        return expression == null ? "no expression" : expression.getText();
    }
}
