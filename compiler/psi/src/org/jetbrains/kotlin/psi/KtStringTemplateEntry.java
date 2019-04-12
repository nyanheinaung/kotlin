/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub;

public abstract class KtStringTemplateEntry extends KtElementImplStub<KotlinPlaceHolderWithTextStub<? extends KtStringTemplateEntry>> {
    public static final KtStringTemplateEntry[] EMPTY_ARRAY = new KtStringTemplateEntry[0];

    public KtStringTemplateEntry(@NotNull ASTNode node) {
        super(node);
    }

    public KtStringTemplateEntry(
            @NotNull KotlinPlaceHolderWithTextStub<? extends KtStringTemplateEntry> stub,
            @NotNull IStubElementType elementType
    ) {
        super(stub, elementType);
    }

    @Nullable
    public KtExpression getExpression() {
        return findChildByClass(KtExpression.class);
    }
}
