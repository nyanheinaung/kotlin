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
import org.jetbrains.kotlin.psi.KtUserType;
import org.jetbrains.kotlin.psi.stubs.KotlinUserTypeStub;
import org.jetbrains.kotlin.psi.stubs.impl.KotlinUserTypeStubImpl;

public class KtUserTypeElementType extends KtStubElementType<KotlinUserTypeStub, KtUserType> {
    public KtUserTypeElementType(@NotNull @NonNls String debugName) {
        super(debugName, KtUserType.class, KotlinUserTypeStub.class);
    }

    @NotNull
    @Override
    public KotlinUserTypeStub createStub(@NotNull KtUserType psi, StubElement parentStub) {
        return new KotlinUserTypeStubImpl((StubElement<?>) parentStub);
    }

    @Override
    public void serialize(@NotNull KotlinUserTypeStub stub, @NotNull StubOutputStream dataStream) {
    }

    @NotNull
    @Override
    public KotlinUserTypeStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) {
        return new KotlinUserTypeStubImpl((StubElement<?>) parentStub);
    }
}
