/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types.expressions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtVisitor;

public abstract class ExpressionTypingVisitor extends KtVisitor<KotlinTypeInfo, ExpressionTypingContext> {

    protected final ExpressionTypingInternals facade;
    protected final ExpressionTypingComponents components;

    protected ExpressionTypingVisitor(@NotNull ExpressionTypingInternals facade) {
        this.facade = facade;
        this.components = facade.getComponents();
    }
}
