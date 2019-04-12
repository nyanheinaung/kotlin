/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtNameReferenceExpression;
import org.jetbrains.kotlin.psi.stubs.KotlinNameReferenceExpressionStub;
import org.jetbrains.kotlin.psi.stubs.impl.KotlinNameReferenceExpressionStubImpl;

import java.io.IOException;

public class KtNameReferenceExpressionElementType extends KtStubElementType<KotlinNameReferenceExpressionStub, KtNameReferenceExpression> {
    public KtNameReferenceExpressionElementType(@NotNull @NonNls String debugName) {
        super(debugName, KtNameReferenceExpression.class, KotlinNameReferenceExpressionStub.class);
    }

    @Override
    public KotlinNameReferenceExpressionStub createStub(@NotNull KtNameReferenceExpression psi, StubElement parentStub) {
        return new KotlinNameReferenceExpressionStubImpl(parentStub, StringRef.fromString(psi.getReferencedName()));
    }

    @Override
    public void serialize(@NotNull KotlinNameReferenceExpressionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeName(stub.getReferencedName());
    }

    @NotNull
    @Override
    public KotlinNameReferenceExpressionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        StringRef referencedName = dataStream.readName();
        return new KotlinNameReferenceExpressionStubImpl(parentStub, referencedName);
    }
}
