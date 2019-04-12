/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtSingleValueToken;
import org.jetbrains.kotlin.lexer.KtTokens;

public enum KtProjectionKind {
    IN(KtTokens.IN_KEYWORD), OUT(KtTokens.OUT_KEYWORD), STAR(KtTokens.MUL), NONE(null);

    private final KtSingleValueToken token;

    KtProjectionKind(@Nullable KtSingleValueToken token) {
        this.token = token;
    }

    @Nullable
    public KtSingleValueToken getToken() {
        return token;
    }
}
