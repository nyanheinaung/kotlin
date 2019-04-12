/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtConstructorCalleeExpression extends KtExpressionImplStub<KotlinPlaceHolderStub<KtConstructorCalleeExpression>> {
    public KtConstructorCalleeExpression(@NotNull ASTNode node) {
        super(node);
    }

    public KtConstructorCalleeExpression(@NotNull KotlinPlaceHolderStub<KtConstructorCalleeExpression> stub) {
        super(stub, KtStubElementTypes.CONSTRUCTOR_CALLEE);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitConstructorCalleeExpression(this, data);
    }

    @Nullable @IfNotParsed
    public KtTypeReference getTypeReference() {
        return getStubOrPsiChild(KtStubElementTypes.TYPE_REFERENCE);
    }

    @Nullable @IfNotParsed
    public KtSimpleNameExpression getConstructorReferenceExpression() {
        KtTypeReference typeReference = getTypeReference();
        if (typeReference == null) {
            return null;
        }
        KtTypeElement typeElement = typeReference.getTypeElement();
        if (!(typeElement instanceof KtUserType)) {
            return null;
        }
        return ((KtUserType) typeElement).getReferenceExpression();
    }

}
