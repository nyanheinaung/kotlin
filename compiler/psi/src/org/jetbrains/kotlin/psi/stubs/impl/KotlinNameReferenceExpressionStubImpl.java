/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtNameReferenceExpression;
import org.jetbrains.kotlin.psi.stubs.KotlinNameReferenceExpressionStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

public class KotlinNameReferenceExpressionStubImpl extends KotlinStubBaseImpl<KtNameReferenceExpression> implements
                                                                                                       KotlinNameReferenceExpressionStub {
    @NotNull
    private final StringRef referencedName;

    public KotlinNameReferenceExpressionStubImpl(StubElement parent, @NotNull StringRef referencedName) {
        super(parent, KtStubElementTypes.REFERENCE_EXPRESSION);
        this.referencedName = referencedName;
    }

    @NotNull
    @Override
    public String getReferencedName() {
        return referencedName.getString();
    }
}
