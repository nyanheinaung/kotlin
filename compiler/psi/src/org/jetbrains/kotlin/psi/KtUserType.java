/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.stubs.KotlinUserTypeStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.Collections;
import java.util.List;

public class KtUserType extends KtElementImplStub<KotlinUserTypeStub> implements KtTypeElement {
    public KtUserType(@NotNull ASTNode node) {
        super(node);
    }

    public KtUserType(@NotNull KotlinUserTypeStub stub) {
        super(stub, KtStubElementTypes.USER_TYPE);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitUserType(this, data);
    }

    @Nullable
    public KtTypeArgumentList getTypeArgumentList() {
        return getStubOrPsiChild(KtStubElementTypes.TYPE_ARGUMENT_LIST);
    }

    @NotNull
    public List<KtTypeProjection> getTypeArguments() {
        // TODO: empty elements in PSI
        KtTypeArgumentList typeArgumentList = getTypeArgumentList();
        return typeArgumentList == null ? Collections.emptyList() : typeArgumentList.getArguments();
    }

    @NotNull
    @Override
    public List<KtTypeReference> getTypeArgumentsAsTypes() {
        List<KtTypeReference> result = Lists.newArrayList();
        for (KtTypeProjection projection : getTypeArguments()) {
            result.add(projection.getTypeReference());
        }
        return result;
    }

    @Nullable @IfNotParsed
    public KtSimpleNameExpression getReferenceExpression() {
        KtNameReferenceExpression nameRefExpr = getStubOrPsiChild(KtStubElementTypes.REFERENCE_EXPRESSION);
        return nameRefExpr != null ? nameRefExpr : getStubOrPsiChild(KtStubElementTypes.ENUM_ENTRY_SUPERCLASS_REFERENCE_EXPRESSION);
    }

    @Nullable
    public KtUserType getQualifier() {
        return getStubOrPsiChild(KtStubElementTypes.USER_TYPE);
    }

    public void deleteQualifier() {
        KtUserType qualifier = getQualifier();
        assert qualifier != null;
        PsiElement dot = findChildByType(KtTokens.DOT);
        assert dot != null;
        qualifier.delete();
        dot.delete();
    }

    @Nullable
    public String getReferencedName() {
        KtSimpleNameExpression referenceExpression = getReferenceExpression();
        return referenceExpression == null ? null : referenceExpression.getReferencedName();
    }
}
