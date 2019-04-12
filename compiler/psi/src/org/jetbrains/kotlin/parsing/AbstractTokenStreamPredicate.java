/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing;

public abstract class AbstractTokenStreamPredicate implements TokenStreamPredicate {

    @Override
    public TokenStreamPredicate or(TokenStreamPredicate other) {
        return new AbstractTokenStreamPredicate() {
            @Override
            public boolean matching(boolean topLevel) {
                if (AbstractTokenStreamPredicate.this.matching(topLevel)) return true;
                return other.matching(topLevel);
            }
        };
    }
}
