/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.Arrays;
import java.util.List;

public class KtInitializerList extends KtElementImplStub<KotlinPlaceHolderStub<KtInitializerList>> {
    public KtInitializerList(@NotNull ASTNode node) {
        super(node);
    }

    public KtInitializerList(@NotNull KotlinPlaceHolderStub<KtInitializerList> stub) {
        super(stub, KtStubElementTypes.INITIALIZER_LIST);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitInitializerList(this, data);
    }

    @NotNull
    public List<KtSuperTypeListEntry> getInitializers() {
        return Arrays.asList(getStubOrPsiChildren(KtStubElementTypes.SUPER_TYPE_LIST_ENTRIES, KtSuperTypeListEntry.ARRAY_FACTORY));
    }
}
