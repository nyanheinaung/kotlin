/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtLiteralStringTemplateEntry extends KtStringTemplateEntry {
    public KtLiteralStringTemplateEntry(@NotNull ASTNode node) {
        super(node);
    }

    public KtLiteralStringTemplateEntry(@NotNull KotlinPlaceHolderWithTextStub<KtLiteralStringTemplateEntry> stub) {
        super(stub, KtStubElementTypes.LITERAL_STRING_TEMPLATE_ENTRY);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitLiteralStringTemplateEntry(this, data);
    }

    @Override
    public String getText() {
        KotlinPlaceHolderWithTextStub<? extends KtStringTemplateEntry> stub = getStub();
        if (stub != null) {
            return stub.text();
        }

        return super.getText();
    }
}
