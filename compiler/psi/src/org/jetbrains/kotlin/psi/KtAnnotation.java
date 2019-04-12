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

import java.util.List;

public class KtAnnotation extends KtElementImplStub<KotlinPlaceHolderStub<KtAnnotation>> {

    public KtAnnotation(@NotNull ASTNode node) {
        super(node);
    }

    public KtAnnotation(KotlinPlaceHolderStub<KtAnnotation> stub) {
        super(stub, KtStubElementTypes.ANNOTATION);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitAnnotation(this, data);
    }

    public List<KtAnnotationEntry> getEntries() {
        return getStubOrPsiChildrenAsList(KtStubElementTypes.ANNOTATION_ENTRY);
    }

    @Nullable
    public KtAnnotationUseSiteTarget getUseSiteTarget() {
        return getStubOrPsiChild(KtStubElementTypes.ANNOTATION_TARGET);
    }

    public void removeEntry(@NotNull KtAnnotationEntry entry) {
        if (getEntries().size() > 1) {
            entry.delete();
        }
        else {
            delete();
        }
    }
}
