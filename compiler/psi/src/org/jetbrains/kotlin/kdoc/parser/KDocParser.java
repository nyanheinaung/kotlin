/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.kdoc.lexer.KDocTokens;

public class KDocParser implements PsiParser {
    @Override
    @NotNull
    public ASTNode parse(IElementType root, PsiBuilder builder) {
        PsiBuilder.Marker rootMarker = builder.mark();
        if (builder.getTokenType() == KDocTokens.START) {
            builder.advanceLexer();
        }
        PsiBuilder.Marker currentSectionMarker = builder.mark();

        // todo: parse KDoc tags, markdown, etc...
        while (!builder.eof()) {
            if (builder.getTokenType() == KDocTokens.TAG_NAME) {
                currentSectionMarker = parseTag(builder, currentSectionMarker);
            }
            else if (builder.getTokenType() == KDocTokens.END) {
                if (currentSectionMarker != null) {
                    currentSectionMarker.done(KDocElementTypes.KDOC_SECTION);
                    currentSectionMarker = null;
                }
                builder.advanceLexer();
            }
            else {
                builder.advanceLexer();
            }
        }

        if (currentSectionMarker != null) {
            currentSectionMarker.done(KDocElementTypes.KDOC_SECTION);
        }
        rootMarker.done(root);
        return builder.getTreeBuilt();
    }

    private static PsiBuilder.Marker parseTag(PsiBuilder builder, PsiBuilder.Marker currentSectionMarker) {
        String tagName = builder.getTokenText();
        KDocKnownTag knownTag = KDocKnownTag.Companion.findByTagName(tagName);
        if (knownTag != null && knownTag.isSectionStart()) {
            currentSectionMarker.done(KDocElementTypes.KDOC_SECTION);
            currentSectionMarker = builder.mark();
        }
        PsiBuilder.Marker tagStart = builder.mark();
        builder.advanceLexer();

        while (!builder.eof() && !isAtEndOfTag(builder)) {
            builder.advanceLexer();
        }
        tagStart.done(KDocElementTypes.KDOC_TAG);
        return currentSectionMarker;
    }

    private static boolean isAtEndOfTag(PsiBuilder builder) {
        if (builder.getTokenType() == KDocTokens.END) {
            return true;
        }
        if (builder.getTokenType() == KDocTokens.LEADING_ASTERISK) {
            int lookAheadCount = 1;
            if (builder.lookAhead(1) == KDocTokens.TEXT) {
                lookAheadCount++;
            }
            if (builder.lookAhead(lookAheadCount) == KDocTokens.TAG_NAME) {
                return true;
            }
        }
        return false;
    }
}
