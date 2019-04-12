/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.Collections;
import java.util.List;

public class KtDynamicType extends KtElementImplStub<KotlinPlaceHolderStub<KtDynamicType>> implements KtTypeElement {
    public KtDynamicType(@NotNull ASTNode node) {
        super(node);
    }

    public KtDynamicType(@NotNull KotlinPlaceHolderStub<KtDynamicType> stub) {
        super(stub, KtStubElementTypes.DYNAMIC_TYPE);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitDynamicType(this, data);
    }

    @NotNull
    @Override
    public List<KtTypeReference> getTypeArgumentsAsTypes() {
        return Collections.emptyList();
    }
}
