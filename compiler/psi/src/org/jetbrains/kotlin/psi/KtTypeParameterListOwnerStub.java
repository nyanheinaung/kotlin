/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.stubs.KotlinStubWithFqName;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.Collections;
import java.util.List;

public abstract class KtTypeParameterListOwnerStub<T extends KotlinStubWithFqName<?>>
        extends KtNamedDeclarationStub<T> implements KtTypeParameterListOwner {
    public KtTypeParameterListOwnerStub(@NotNull T stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public KtTypeParameterListOwnerStub(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @Nullable
    public KtTypeParameterList getTypeParameterList() {
        return getStubOrPsiChild(KtStubElementTypes.TYPE_PARAMETER_LIST);
    }

    @Override
    @Nullable
    public KtTypeConstraintList getTypeConstraintList() {
        return getStubOrPsiChild(KtStubElementTypes.TYPE_CONSTRAINT_LIST);
    }

    @Override
    @NotNull
    public List<KtTypeConstraint> getTypeConstraints() {
        KtTypeConstraintList typeConstraintList = getTypeConstraintList();
        if (typeConstraintList == null) {
            return Collections.emptyList();
        }
        return typeConstraintList.getConstraints();
    }

    @Override
    @NotNull
    public List<KtTypeParameter> getTypeParameters() {
        KtTypeParameterList list = getTypeParameterList();
        if (list == null) return Collections.emptyList();

        return list.getParameters();
    }
}
