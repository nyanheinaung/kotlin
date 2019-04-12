/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.when;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.codegen.ExpressionCodegen;
import org.jetbrains.kotlin.psi.KtWhenExpression;
import org.jetbrains.kotlin.resolve.constants.ConstantValue;
import org.jetbrains.org.objectweb.asm.Label;

public class IntegralConstantsSwitchCodegen extends SwitchCodegen {
    public IntegralConstantsSwitchCodegen(
            @NotNull KtWhenExpression expression,
            boolean isStatement,
            boolean isExhaustive,
            @NotNull ExpressionCodegen codegen
    ) {
        super(expression, isStatement, isExhaustive, codegen, null);
    }

    @Override
    protected void processConstant(@NotNull ConstantValue<?> constant, @NotNull Label entryLabel) {
        assert constant.getValue() != null : "constant value should not be null";
        int value = (constant.getValue() instanceof Number)
                    ? ((Number) constant.getValue()).intValue()
                    : ((Character) constant.getValue()).charValue();

        putTransitionOnce(value, entryLabel);
    }

    @Override
    protected void generateSubjectValueToIndex() {
        // Do nothing: subject is an int value
    }
}
