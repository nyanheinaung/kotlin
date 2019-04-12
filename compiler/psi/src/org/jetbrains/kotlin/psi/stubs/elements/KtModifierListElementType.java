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
import org.jetbrains.kotlin.psi.KtModifierList;
import org.jetbrains.kotlin.psi.stubs.KotlinModifierListStub;
import org.jetbrains.kotlin.psi.stubs.impl.KotlinModifierListStubImpl;

import java.io.IOException;

import static org.jetbrains.kotlin.psi.stubs.impl.ModifierMaskUtils.computeMaskFromModifierList;

public class KtModifierListElementType<T extends KtModifierList> extends KtStubElementType<KotlinModifierListStub, T> {
    public KtModifierListElementType(@NotNull @NonNls String debugName, @NotNull Class<T> psiClass) {
        super(debugName, psiClass, KotlinModifierListStub.class);
    }

    @Override
    public KotlinModifierListStub createStub(@NotNull T psi, StubElement parentStub) {
        return new KotlinModifierListStubImpl(parentStub, computeMaskFromModifierList(psi), this);
    }

    @Override
    public void serialize(@NotNull KotlinModifierListStub stub, @NotNull StubOutputStream dataStream) throws IOException {
        int mask = ((KotlinModifierListStubImpl) stub).getMask();
        dataStream.writeVarInt(mask);
    }

    @NotNull
    @Override
    public KotlinModifierListStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
        int mask = dataStream.readVarInt();
        return new KotlinModifierListStubImpl(parentStub, mask, this);
    }
}
