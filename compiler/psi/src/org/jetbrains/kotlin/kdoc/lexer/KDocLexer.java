/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.lexer;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;

import java.io.Reader;

public class KDocLexer extends MergingLexerAdapter {
    public KDocLexer() {
        super(
                new FlexAdapter(
                        new _KDocLexer((Reader) null)
                ),
                TokenSet.create(KDocTokens.TEXT, KDocTokens.CODE_BLOCK_TEXT)
        );
    }
}
