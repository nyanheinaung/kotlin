/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiArrayType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.load.java.structure.JavaArrayType;

public class JavaArrayTypeImpl extends JavaTypeImpl<PsiArrayType> implements JavaArrayType {
    public JavaArrayTypeImpl(@NotNull PsiArrayType psiArrayType) {
        super(psiArrayType);
    }

    @Override
    @NotNull
    public JavaTypeImpl<?> getComponentType() {
        return JavaTypeImpl.create(getPsi().getComponentType());
    }
}
