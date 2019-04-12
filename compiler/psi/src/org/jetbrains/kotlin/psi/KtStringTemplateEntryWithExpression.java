/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderWithTextStub;

public abstract class KtStringTemplateEntryWithExpression extends KtStringTemplateEntry {
    public KtStringTemplateEntryWithExpression(@NotNull ASTNode node) {
        super(node);
    }

    public KtStringTemplateEntryWithExpression(
            @NotNull KotlinPlaceHolderWithTextStub<? extends KtStringTemplateEntryWithExpression> stub,
            @NotNull IStubElementType elementType) {
        super(stub, elementType);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitStringTemplateEntryWithExpression(this, data);
    }
}
