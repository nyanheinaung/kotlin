/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.List;

public class KtTypeArgumentList extends KtElementImplStub<KotlinPlaceHolderStub<KtTypeArgumentList>> {
    public KtTypeArgumentList(@NotNull ASTNode node) {
        super(node);
    }

    public KtTypeArgumentList(@NotNull KotlinPlaceHolderStub<KtTypeArgumentList> stub) {
        super(stub, KtStubElementTypes.TYPE_ARGUMENT_LIST);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitTypeArgumentList(this, data);
    }

    @NotNull
    public List<KtTypeProjection> getArguments() {
        return getStubOrPsiChildrenAsList(KtStubElementTypes.TYPE_PROJECTION);
    }

    @NotNull
    public KtTypeProjection addArgument(@NotNull KtTypeProjection typeArgument) {
        return EditCommaSeparatedListHelper.INSTANCE.addItem(this, getArguments(), typeArgument, KtTokens.LT);
    }
}
