/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing;

public class FirstBefore extends AbstractTokenStreamPattern {
    private final TokenStreamPredicate lookFor;
    private final TokenStreamPredicate stopAt;

    public FirstBefore(TokenStreamPredicate lookFor, TokenStreamPredicate stopAt) {
        this.lookFor = lookFor;
        this.stopAt = stopAt;
    }

    @Override
    public boolean processToken(int offset, boolean topLevel) {
        if (lookFor.matching(topLevel)) {
            lastOccurrence = offset;
            return true;
        }
        if (stopAt.matching(topLevel)) {
            return true;
        }
        return false;
    }
}
