/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.List;

public class KtTypeConstraintList extends KtElementImplStub<KotlinPlaceHolderStub<KtTypeConstraintList>> {
    public KtTypeConstraintList(@NotNull ASTNode node) {
        super(node);
    }

    public KtTypeConstraintList(@NotNull KotlinPlaceHolderStub<KtTypeConstraintList> stub) {
        super(stub, KtStubElementTypes.TYPE_CONSTRAINT_LIST);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitTypeConstraintList(this, data);
    }

    @NotNull
    public List<KtTypeConstraint> getConstraints() {
        return getStubOrPsiChildrenAsList(KtStubElementTypes.TYPE_CONSTRAINT);
    }
}
