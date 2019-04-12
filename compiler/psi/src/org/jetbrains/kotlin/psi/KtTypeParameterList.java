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

public class KtTypeParameterList extends KtElementImplStub<KotlinPlaceHolderStub<KtTypeParameterList>> {
    public KtTypeParameterList(@NotNull ASTNode node) {
        super(node);
    }

    public KtTypeParameterList(@NotNull KotlinPlaceHolderStub<KtTypeParameterList> stub) {
        super(stub, KtStubElementTypes.TYPE_PARAMETER_LIST);
    }

    @NotNull
    public List<KtTypeParameter> getParameters() {
        return getStubOrPsiChildrenAsList(KtStubElementTypes.TYPE_PARAMETER);
    }

    @NotNull
    public KtTypeParameter addParameter(@NotNull KtTypeParameter typeParameter) {
        return EditCommaSeparatedListHelper.INSTANCE.addItem(this, getParameters(), typeParameter, KtTokens.LT);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitTypeParameterList(this, data);
    }
}
