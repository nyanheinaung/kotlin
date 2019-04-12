/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtSuperTypeListEntry extends KtElementImplStub<KotlinPlaceHolderStub<? extends KtSuperTypeListEntry>> {
    private static final KtSuperTypeListEntry[] EMPTY_ARRAY = new KtSuperTypeListEntry[0];

    public static ArrayFactory<KtSuperTypeListEntry> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new KtSuperTypeListEntry[count];

    public KtSuperTypeListEntry(@NotNull ASTNode node) {
        super(node);
    }

    public KtSuperTypeListEntry(
            @NotNull KotlinPlaceHolderStub<? extends KtSuperTypeListEntry> stub,
            @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitSuperTypeListEntry(this, data);
    }

    @Nullable
    public KtTypeReference getTypeReference() {
        return getStubOrPsiChild(KtStubElementTypes.TYPE_REFERENCE);
    }

    @Nullable
    public KtUserType getTypeAsUserType() {
        KtTypeReference reference = getTypeReference();
        if (reference != null) {
            KtTypeElement element = reference.getTypeElement();
            if (element instanceof KtUserType) {
                return ((KtUserType) element);
            }
        }
        return null;
    }
}
