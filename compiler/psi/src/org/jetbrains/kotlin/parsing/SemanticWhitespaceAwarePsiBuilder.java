/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.parsing;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

public interface SemanticWhitespaceAwarePsiBuilder extends PsiBuilder {
    // TODO: comments go to wrong place when an empty element is created, see IElementType.isLeftBound()

    boolean newlineBeforeCurrentToken();
    void disableNewlines();
    void enableNewlines();
    void restoreNewlinesState();

    void restoreJoiningComplexTokensState();
    void enableJoiningComplexTokens();
    void disableJoiningComplexTokens();

    boolean isWhitespaceOrComment(@NotNull IElementType elementType);
}
