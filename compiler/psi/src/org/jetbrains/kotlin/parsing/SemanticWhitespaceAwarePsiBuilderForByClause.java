/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing;

public class SemanticWhitespaceAwarePsiBuilderForByClause extends SemanticWhitespaceAwarePsiBuilderAdapter {

    private int stackSize = 0;

    public SemanticWhitespaceAwarePsiBuilderForByClause(SemanticWhitespaceAwarePsiBuilder builder) {
        super(builder);
    }

    @Override
    public void disableNewlines() {
        super.disableNewlines();
        stackSize++;
    }

    @Override
    public void enableNewlines() {
        super.enableNewlines();
        stackSize++;
    }

    @Override
    public void restoreNewlinesState() {
        super.restoreNewlinesState();
        stackSize--;
    }

    public int getStackSize() {
        return stackSize;
    }
}
