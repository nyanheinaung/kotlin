/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.elements;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtPropertyAccessor;
import org.jetbrains.kotlin.psi.stubs.KotlinPropertyAccessorStub;
import org.jetbrains.kotlin.psi.stubs.impl.KotlinPropertyAccessorStubImpl;

import java.io.IOException;

public class KtPropertyAccessorElementType extends KtStubElementType<KotlinPropertyAccessorStub, KtPropertyAccessor> {
    public KtPropertyAccessorElementType(@NotNull @NonNls String debugName) {
        super(debugName, KtPropertyAccessor.class, KotlinPropertyAccessorStub.class);
    }

    @Override
    public KotlinPropertyAccessorStub createStub(@NotNull KtPropertyAccessor psi, StubElement parentStub) {
        return new KotlinPropertyAccessorStubImpl(parentStub, psi.isGetter(), psi.hasBody(), psi.hasBlockBody());
    }

    @Override
    public void serialize(@NotNull KotlinPropertyAccessorStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(stub.isGetter());
        dataStream.writeBoolean(stub.hasBody());
        dataStream.writeBoolean(stub.hasBlockBody());
    }

    @NotNull
    @Override
    public KotlinPropertyAccessorStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        boolean isGetter = dataStream.readBoolean();
        boolean hasBody = dataStream.readBoolean();
        boolean hasBlockBody = dataStream.readBoolean();
        return new KotlinPropertyAccessorStubImpl(parentStub, isGetter, hasBody, hasBlockBody);
    }
}
