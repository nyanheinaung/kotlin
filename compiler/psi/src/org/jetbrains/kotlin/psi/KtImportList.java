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

public class KtImportList extends KtElementImplStub<KotlinPlaceHolderStub<KtImportList>> {

    public KtImportList(@NotNull ASTNode node) {
        super(node);
    }

    public KtImportList(@NotNull KotlinPlaceHolderStub<KtImportList> stub) {
        super(stub, KtStubElementTypes.IMPORT_LIST);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitImportList(this, data);
    }

    @NotNull
    public List<KtImportDirective> getImports() {
        return getStubOrPsiChildrenAsList(KtStubElementTypes.IMPORT_DIRECTIVE);
    }
}
