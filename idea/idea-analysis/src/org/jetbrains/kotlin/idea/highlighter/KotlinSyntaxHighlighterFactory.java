/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.highlighter;

import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import org.jetbrains.annotations.NotNull;

public class KotlinSyntaxHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {
    @Override
    @NotNull
    protected SyntaxHighlighter createHighlighter() {
      return new KotlinHighlighter();
    }
}
