/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiPrimitiveType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.builtins.PrimitiveType;
import org.jetbrains.kotlin.load.java.structure.JavaPrimitiveType;
import org.jetbrains.kotlin.resolve.jvm.JvmPrimitiveType;

public class JavaPrimitiveTypeImpl extends JavaTypeImpl<PsiPrimitiveType> implements JavaPrimitiveType {
    public JavaPrimitiveTypeImpl(@NotNull PsiPrimitiveType psiPrimitiveType) {
        super(psiPrimitiveType);
    }

    @Override
    @Nullable
    public PrimitiveType getType() {
        String text = getPsi().getCanonicalText();
        return "void".equals(text) ? null : JvmPrimitiveType.get(text).getPrimitiveType();
    }
}
