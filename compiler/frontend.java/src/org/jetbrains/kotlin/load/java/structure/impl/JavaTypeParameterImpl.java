/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiAnnotationOwner;
import com.intellij.psi.PsiTypeParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.load.java.structure.JavaClassifierType;
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter;
import org.jetbrains.kotlin.name.Name;
import org.jetbrains.kotlin.name.SpecialNames;

import java.util.Collection;

import static org.jetbrains.kotlin.load.java.structure.impl.JavaElementCollectionFromPsiArrayUtil.classifierTypes;

public class JavaTypeParameterImpl extends JavaClassifierImpl<PsiTypeParameter> implements JavaTypeParameter {
    public JavaTypeParameterImpl(@NotNull PsiTypeParameter psiTypeParameter) {
        super(psiTypeParameter);
    }

    @NotNull
    @Override
    public Name getName() {
        return SpecialNames.safeIdentifier(getPsi().getName());
    }

    @Override
    @NotNull
    public Collection<JavaClassifierType> getUpperBounds() {
        return classifierTypes(getPsi().getExtendsList().getReferencedTypes());
    }

    @Nullable
    @Override
    public PsiAnnotationOwner getAnnotationOwnerPsi() {
        return getPsi();
    }
}
