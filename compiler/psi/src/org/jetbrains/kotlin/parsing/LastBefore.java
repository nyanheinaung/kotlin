/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing;

public class LastBefore extends AbstractTokenStreamPattern {
    private final boolean dontStopRightAfterOccurrence;
    private final TokenStreamPredicate lookFor;
    private final TokenStreamPredicate stopAt;

    private boolean previousLookForResult;

    public LastBefore(TokenStreamPredicate lookFor, TokenStreamPredicate stopAt, boolean dontStopRightAfterOccurrence) {
        this.lookFor = lookFor;
        this.stopAt = stopAt;
        this.dontStopRightAfterOccurrence = dontStopRightAfterOccurrence;
    }

    public LastBefore(TokenStreamPredicate lookFor, TokenStreamPredicate stopAt) {
        this(lookFor, stopAt, false);
    }

    @Override
    public boolean processToken(int offset, boolean topLevel) {
        boolean lookForResult = lookFor.matching(topLevel);
        if (lookForResult) {
            lastOccurrence = offset;
        }
        if (stopAt.matching(topLevel)) {
            if (topLevel
                && (!dontStopRightAfterOccurrence
                    || !previousLookForResult)) return true;
        }
        previousLookForResult = lookForResult;
        return false;
    }
}
