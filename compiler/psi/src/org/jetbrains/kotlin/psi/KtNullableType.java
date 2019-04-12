/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.Collections;
import java.util.List;

public class KtNullableType extends KtElementImplStub<KotlinPlaceHolderStub<KtNullableType>> implements KtTypeElement {
    public KtNullableType(@NotNull ASTNode node) {
        super(node);
    }

    public KtNullableType(@NotNull KotlinPlaceHolderStub<KtNullableType> stub) {
        super(stub, KtStubElementTypes.NULLABLE_TYPE);
    }

    @NotNull
    public ASTNode getQuestionMarkNode() {
        return getNode().findChildByType(KtTokens.QUEST);
    }

    @NotNull
    @Override
    public List<KtTypeReference> getTypeArgumentsAsTypes() {
        KtTypeElement innerType = getInnerType();
        return innerType == null ? Collections.emptyList() : innerType.getTypeArgumentsAsTypes();
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitNullableType(this, data);
    }

    @Nullable
    @IfNotParsed
    public KtTypeElement getInnerType() {
        return KtStubbedPsiUtil.getStubOrPsiChild(this, KtStubElementTypes.TYPE_ELEMENT_TYPES, KtTypeElement.ARRAY_FACTORY);
    }

    @Nullable
    public KtModifierList getModifierList() {
        return getStubOrPsiChild(KtStubElementTypes.MODIFIER_LIST);
    }

    @NotNull
    public List<KtAnnotationEntry> getAnnotationEntries() {
        KtModifierList modifierList = getModifierList();
        return modifierList != null ? modifierList.getAnnotationEntries() : Collections.emptyList();
    }
}
