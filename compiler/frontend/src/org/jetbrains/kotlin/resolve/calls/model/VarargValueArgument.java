/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtExpression;
import org.jetbrains.kotlin.psi.ValueArgument;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VarargValueArgument implements ResolvedValueArgument {
    private final List<ValueArgument> arguments;

    public VarargValueArgument() {
        this.arguments = new ArrayList<>();
    }

    public VarargValueArgument(@NotNull List<? extends ValueArgument> arguments) {
        this.arguments = new ArrayList<>(arguments);
    }

    public void addArgument(@NotNull ValueArgument argument) {
        arguments.add(argument);
    }

    @Override
    @NotNull
    public List<ValueArgument> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("vararg:{");
        for (Iterator<ValueArgument> iterator = arguments.iterator(); iterator.hasNext(); ) {
            ValueArgument valueArgument = iterator.next();
            KtExpression expression = valueArgument.getArgumentExpression();
            builder.append(expression == null ? "no expression" : expression.getText());
            if (iterator.hasNext()) {
                builder.append(", ");
            }
        }
        return builder.append("}").toString();
    }
}
