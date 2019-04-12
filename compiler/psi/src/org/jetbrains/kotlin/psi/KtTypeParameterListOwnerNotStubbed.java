/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.KtNodeTypes;

import java.util.Collections;
import java.util.List;

// TODO: Remove when all implementations of JetTypeParameterListOwner get stubs
@Deprecated
abstract class KtTypeParameterListOwnerNotStubbed extends KtNamedDeclarationNotStubbed implements KtTypeParameterListOwner {
    public KtTypeParameterListOwnerNotStubbed(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    @Nullable
    public KtTypeParameterList getTypeParameterList() {
        return (KtTypeParameterList) findChildByType(KtNodeTypes.TYPE_PARAMETER_LIST);
    }

    @Override
    @Nullable
    public KtTypeConstraintList getTypeConstraintList() {
        return (KtTypeConstraintList) findChildByType(KtNodeTypes.TYPE_CONSTRAINT_LIST);
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
