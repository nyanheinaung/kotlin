/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.load.java.structure.JavaField;
import org.jetbrains.kotlin.load.java.structure.JavaType;

public class JavaFieldImpl extends JavaMemberImpl<PsiField> implements JavaField {
    public JavaFieldImpl(@NotNull PsiField psiField) {
        super(psiField);
    }

    @Override
    public boolean isEnumEntry() {
        return getPsi() instanceof PsiEnumConstant;
    }

    @Override
    @NotNull
    public JavaType getType() {
        return JavaTypeImpl.create(getPsi().getType());
    }

    @Nullable
    @Override
    public Object getInitializerValue() {
        return getPsi().computeConstantValue();
    }

    @Override
    public boolean getHasConstantNotNullInitializer() {
        // PsiUtil.isCompileTimeConstant returns false for null-initialized fields,
        // see com.intellij.psi.util.IsConstantExpressionVisitor.visitLiteralExpression()
        return PsiUtil.isCompileTimeConstant((PsiVariable) getPsi());
    }
}
