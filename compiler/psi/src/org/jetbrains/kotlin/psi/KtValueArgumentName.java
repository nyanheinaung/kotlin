/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.KtNodeTypes;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KtValueArgumentName extends KtElementImplStub<KotlinPlaceHolderStub<KtValueArgumentName>> implements ValueArgumentName {
    public KtValueArgumentName(@NotNull ASTNode node) {
        super(node);
    }

    public KtValueArgumentName(@NotNull KotlinPlaceHolderStub<KtValueArgumentName> stub) {
        super(stub, KtStubElementTypes.VALUE_ARGUMENT_NAME);
    }

    @Override
    @NotNull
    public KtSimpleNameExpression getReferenceExpression() {
        return (KtSimpleNameExpression) findChildByType(KtNodeTypes.REFERENCE_EXPRESSION);
    }

    @NotNull
    @Override
    public Name getAsName() {
        return getReferenceExpression().getReferencedNameAsName();
    }
}
