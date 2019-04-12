/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.lexer;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class KtSingleValueToken extends KtToken {

    private final String myValue;

    public KtSingleValueToken(@NotNull @NonNls String debugName, @NotNull @NonNls String value) {
        super(debugName);
        myValue = value;
    }

    @NotNull @NonNls
    public String getValue() {
        return myValue;
    }
}
