/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.surroundWith;

import com.intellij.lang.surroundWith.Surrounder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.codeInsight.surroundWith.expression.KotlinExpressionSurroundDescriptor;

public class KotlinDebuggerExpressionSurroundDescriptor extends KotlinExpressionSurroundDescriptor {

    private static final Surrounder[] SURROUNDERS = {
            new KotlinRuntimeTypeCastSurrounder()
    };

    @Override
    @NotNull
    public Surrounder[] getSurrounders() {
        return SURROUNDERS;
    }
}
