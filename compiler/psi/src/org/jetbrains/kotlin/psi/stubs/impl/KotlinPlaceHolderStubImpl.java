/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.kotlin.psi.KtElementImplStub;
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub;

public class KotlinPlaceHolderStubImpl<T extends KtElementImplStub<? extends StubElement<?>>> extends KotlinStubBaseImpl<T>
        implements KotlinPlaceHolderStub<T> {
    public KotlinPlaceHolderStubImpl(StubElement parent, IStubElementType elementType) {
        //noinspection unchecked
        super(parent, elementType);
    }
}
