/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtSuperTypeEntry extends KtSuperTypeListEntry {
    public KtSuperTypeEntry(@NotNull ASTNode node) {
        super(node);
    }

    public KtSuperTypeEntry(@NotNull KotlinPlaceHolderStub<? extends KtSuperTypeListEntry> stub) {
        super(stub, KtStubElementTypes.SUPER_TYPE_ENTRY);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitSuperTypeEntry(this, data);
    }
}
