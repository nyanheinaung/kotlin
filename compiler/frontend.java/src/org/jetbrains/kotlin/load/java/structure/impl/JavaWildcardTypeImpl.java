/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiType;
import com.intellij.psi.PsiWildcardType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.load.java.structure.JavaWildcardType;

public class JavaWildcardTypeImpl extends JavaTypeImpl<PsiWildcardType> implements JavaWildcardType {
    public JavaWildcardTypeImpl(@NotNull PsiWildcardType psiWildcardType) {
        super(psiWildcardType);
    }

    @Override
    @Nullable
    public JavaTypeImpl<?> getBound() {
        PsiType bound = getPsi().getBound();
        return bound == null ? null : create(bound);
    }

    @Override
    public boolean isExtends() {
        return getPsi().isExtends();
    }
}
