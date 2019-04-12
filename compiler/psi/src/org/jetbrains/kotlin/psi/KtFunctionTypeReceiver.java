/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtFunctionTypeReceiver extends KtElementImplStub<KotlinPlaceHolderStub<KtFunctionTypeReceiver>> {
    public KtFunctionTypeReceiver(@NotNull ASTNode node) {
        super(node);
    }

    public KtFunctionTypeReceiver(@NotNull KotlinPlaceHolderStub<KtFunctionTypeReceiver> stub) {
        super(stub, KtStubElementTypes.FUNCTION_TYPE_RECEIVER);
    }

    @NotNull
    public KtTypeReference getTypeReference() {
        return getRequiredStubOrPsiChild(KtStubElementTypes.TYPE_REFERENCE);
    }
}
