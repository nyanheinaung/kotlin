/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtEscapeStringTemplateEntry extends KtStringTemplateEntry {
    public KtEscapeStringTemplateEntry(@NotNull ASTNode node) {
        super(node);
    }

    public KtEscapeStringTemplateEntry(@NotNull KotlinPlaceHolderWithTextStub<KtEscapeStringTemplateEntry> stub) {
        super(stub, KtStubElementTypes.ESCAPE_STRING_TEMPLATE_ENTRY);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitEscapeStringTemplateEntry(this, data);
    }

    public String getUnescapedValue() {
        return StringUtil.unescapeStringCharacters(getText());
    }
}
