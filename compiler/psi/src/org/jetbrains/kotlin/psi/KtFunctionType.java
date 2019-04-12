/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtToken;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KtFunctionType extends KtElementImplStub<KotlinPlaceHolderStub<KtFunctionType>> implements KtTypeElement {

    public static final KtToken RETURN_TYPE_SEPARATOR = KtTokens.ARROW;

    public KtFunctionType(@NotNull ASTNode node) {
        super(node);
    }

    public KtFunctionType(@NotNull KotlinPlaceHolderStub<KtFunctionType> stub) {
        super(stub, KtStubElementTypes.FUNCTION_TYPE);
    }

    @NotNull
    @Override
    public List<KtTypeReference> getTypeArgumentsAsTypes() {
        ArrayList<KtTypeReference> result = Lists.newArrayList();
        KtTypeReference receiverTypeRef = getReceiverTypeReference();
        if (receiverTypeRef != null) {
            result.add(receiverTypeRef);
        }
        for (KtParameter jetParameter : getParameters()) {
            result.add(jetParameter.getTypeReference());
        }
        KtTypeReference returnTypeRef = getReturnTypeReference();
        if (returnTypeRef != null) {
            result.add(returnTypeRef);
        }
        return result;
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitFunctionType(this, data);
    }

    @Nullable
    public KtParameterList getParameterList() {
        return getStubOrPsiChild(KtStubElementTypes.VALUE_PARAMETER_LIST);
    }

    @NotNull
    public List<KtParameter> getParameters() {
        KtParameterList list = getParameterList();
        return list != null ? list.getParameters() : Collections.emptyList();
    }

    @Nullable
    public KtFunctionTypeReceiver getReceiver() {
        return getStubOrPsiChild(KtStubElementTypes.FUNCTION_TYPE_RECEIVER);
    }

    @Nullable
    public KtTypeReference getReceiverTypeReference() {
        KtFunctionTypeReceiver receiverDeclaration = getReceiver();
        if (receiverDeclaration == null) {
            return null;
        }
        return receiverDeclaration.getTypeReference();
    }

    @Nullable
    public KtTypeReference getReturnTypeReference() {
        return getStubOrPsiChild(KtStubElementTypes.TYPE_REFERENCE);
    }
}
