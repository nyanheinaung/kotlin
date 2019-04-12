/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.load.java.structure.JavaConstructor;
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter;
import org.jetbrains.kotlin.load.java.structure.JavaValueParameter;

import java.util.List;

import static org.jetbrains.kotlin.load.java.structure.impl.JavaElementCollectionFromPsiArrayUtil.typeParameters;
import static org.jetbrains.kotlin.load.java.structure.impl.JavaElementCollectionFromPsiArrayUtil.valueParameters;

public class JavaConstructorImpl extends JavaMemberImpl<PsiMethod> implements JavaConstructor {
    public JavaConstructorImpl(@NotNull PsiMethod psiMethod) {
        super(psiMethod);
        assert psiMethod.isConstructor() :
                "PsiMethod which is not a constructor should not be wrapped in JavaConstructorImpl: " +
                psiMethod.getName() + " " + psiMethod.getClass().getName();
    }

    @NotNull
    @Override
    public List<JavaValueParameter> getValueParameters() {
        return valueParameters(getPsi().getParameterList().getParameters());
    }

    @NotNull
    @Override
    public List<JavaTypeParameter> getTypeParameters() {
        return typeParameters(getPsi().getTypeParameters());
    }
}
