/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.lexer;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/* Modifier keyword is a keyword that can be used in annotation position as part of modifier list.*/
public final class KtModifierKeywordToken extends KtKeywordToken {

    /**
     * Generate keyword (identifier that has a keyword meaning in all possible contexts)
     */
    public static KtModifierKeywordToken keywordModifier(String value) {
        return new KtModifierKeywordToken(value, value, false);
    }

    /**
     * Generate soft keyword (identifier that has a keyword meaning only in some contexts)
     */
    public static KtModifierKeywordToken softKeywordModifier(String value) {
        return new KtModifierKeywordToken(value, value, true);
    }

    private KtModifierKeywordToken(@NotNull @NonNls String debugName, @NotNull @NonNls String value, boolean isSoft) {
        super(debugName, value, isSoft);
    }
}
