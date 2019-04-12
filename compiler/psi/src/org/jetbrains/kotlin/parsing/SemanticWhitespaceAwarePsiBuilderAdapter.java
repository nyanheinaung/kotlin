/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing;

import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public class SemanticWhitespaceAwarePsiBuilderAdapter extends PsiBuilderAdapter implements SemanticWhitespaceAwarePsiBuilder {

    private final SemanticWhitespaceAwarePsiBuilder myBuilder;

    public SemanticWhitespaceAwarePsiBuilderAdapter(SemanticWhitespaceAwarePsiBuilder builder) {
        super(builder);
        this.myBuilder = builder;
    }

    @Override
    public boolean newlineBeforeCurrentToken() {
        return myBuilder.newlineBeforeCurrentToken();
    }

    @Override
    public void disableNewlines() {
        myBuilder.disableNewlines();
    }

    @Override
    public void enableNewlines() {
        myBuilder.enableNewlines();
    }

    @Override
    public void restoreNewlinesState() {
        myBuilder.restoreNewlinesState();
    }

    @Override
    public void restoreJoiningComplexTokensState() {
        myBuilder.restoreJoiningComplexTokensState();
    }

    @Override
    public void enableJoiningComplexTokens() {
        myBuilder.enableJoiningComplexTokens();
    }

    @Override
    public void disableJoiningComplexTokens() {
        myBuilder.disableJoiningComplexTokens();
    }

    @Override
    public boolean isWhitespaceOrComment(@NotNull IElementType elementType) {
        return myBuilder.isWhitespaceOrComment(elementType);
    }
}
