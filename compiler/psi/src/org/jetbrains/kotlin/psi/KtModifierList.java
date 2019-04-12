/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtModifierKeywordToken;
import org.jetbrains.kotlin.psi.psiUtil.KtPsiUtilKt;
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.List;

public abstract class KtModifierList extends KtElementImplStub<KotlinModifierListStub> implements KtAnnotationsContainer {

    public KtModifierList(@NotNull KotlinModifierListStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public KtModifierList(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitModifierList(this, data);
    }

    @NotNull
    public List<KtAnnotation> getAnnotations() {
        return getStubOrPsiChildrenAsList(KtStubElementTypes.ANNOTATION);
    }

    @NotNull
    public List<KtAnnotationEntry> getAnnotationEntries() {
        return KtPsiUtilKt.collectAnnotationEntriesFromStubOrPsi(this);
    }

    public boolean hasModifier(@NotNull KtModifierKeywordToken tokenType) {
        KotlinModifierListStub stub = getStub();
        if (stub != null) {
            return stub.hasModifier(tokenType);
        }
        return getModifier(tokenType) != null;
    }

    @Nullable
    public PsiElement getModifier(@NotNull KtModifierKeywordToken tokenType) {
        return findChildByType(tokenType);
    }

    public PsiElement getOwner() {
        return getParentByStub();
    }

    @Override
    public void deleteChildInternal(@NotNull ASTNode child) {
        super.deleteChildInternal(child);
        if (getFirstChild() == null) {
            delete();
        }
    }
}
