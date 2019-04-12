/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.lexer;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class KtKeywordToken extends KtSingleValueToken {

    /**
     * Generate keyword (identifier that has a keyword meaning in all possible contexts)
     */
    public static KtKeywordToken keyword(String value) {
        return keyword(value, value);
    }

    public static KtKeywordToken keyword(String debugName, String value) {
        return new KtKeywordToken(debugName, value, false);
    }

    /**
     * Generate soft keyword (identifier that has a keyword meaning only in some contexts)
     */
    public static KtKeywordToken softKeyword(String value) {
        return new KtKeywordToken(value, value, true);
    }

    private final boolean myIsSoft;

    protected KtKeywordToken(@NotNull @NonNls String debugName, @NotNull @NonNls String value, boolean isSoft) {
        super(debugName, value);
        myIsSoft = isSoft;
    }

    public boolean isSoft() {
        return myIsSoft;
    }
}
