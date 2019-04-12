/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.*;

public class CodegenStatementVisitor extends KtVisitor<StackValue, StackValue> {
    private final ExpressionCodegen codegen;

    public CodegenStatementVisitor(ExpressionCodegen codegen) {
        this.codegen = codegen;
    }

    @Override
    public StackValue visitKtElement(@NotNull KtElement element, StackValue receiver) {
        return element.accept(codegen, receiver);
    }

    @Override
    public StackValue visitIfExpression(@NotNull KtIfExpression expression, StackValue receiver) {
        return codegen.generateIfExpression(expression, true);
    }

    @Override
    public StackValue visitTryExpression(@NotNull KtTryExpression expression, StackValue data) {
        return codegen.generateTryExpression(expression, true);
    }

    @Override
    public StackValue visitNamedFunction(@NotNull KtNamedFunction function, StackValue data) {
        return codegen.visitNamedFunction(function, data, true);
    }

    @Override
    public StackValue visitWhenExpression(@NotNull KtWhenExpression expression, StackValue data) {
        return codegen.generateWhenExpression(expression, true);
    }

    @Override
    public StackValue visitBlockExpression(@NotNull KtBlockExpression expression, StackValue data) {
        return codegen.generateBlock(expression, true);
    }

    @Override
    public StackValue visitLabeledExpression(@NotNull KtLabeledExpression expression, StackValue receiver) {
        KtExpression baseExpression = expression.getBaseExpression();
        assert baseExpression != null : "Label expression should have base one: " + expression.getText();
        return baseExpression.accept(this, receiver);
    }
}
