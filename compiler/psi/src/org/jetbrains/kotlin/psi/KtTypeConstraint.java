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

public class KtTypeConstraint extends KtElementImplStub<KotlinPlaceHolderStub<KtTypeConstraint>> {
    public KtTypeConstraint(@NotNull ASTNode node) {
        super(node);
    }

    public KtTypeConstraint(@NotNull KotlinPlaceHolderStub<KtTypeConstraint> stub) {
        super(stub, KtStubElementTypes.TYPE_CONSTRAINT);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitTypeConstraint(this, data);
    }

    @Nullable @IfNotParsed
    public KtSimpleNameExpression getSubjectTypeParameterName() {
        return getStubOrPsiChild(KtStubElementTypes.REFERENCE_EXPRESSION);
    }

    @Nullable @IfNotParsed
    public KtTypeReference getBoundTypeReference() {
        return getStubOrPsiChild(KtStubElementTypes.TYPE_REFERENCE);
    }
}
