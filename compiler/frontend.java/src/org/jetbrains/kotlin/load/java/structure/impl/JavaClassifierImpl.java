/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiTypeParameter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation;
import org.jetbrains.kotlin.load.java.structure.JavaClassifier;
import org.jetbrains.kotlin.name.FqName;

import java.util.Collection;

public abstract class JavaClassifierImpl<Psi extends PsiClass> extends JavaElementImpl<Psi> implements JavaClassifier, JavaAnnotationOwnerImpl {
    protected JavaClassifierImpl(@NotNull Psi psiClass) {
        super(psiClass);
    }

    @NotNull
    /* package */ static JavaClassifierImpl<?> create(@NotNull PsiClass psiClass) {
        if (psiClass instanceof PsiTypeParameter) {
            return new JavaTypeParameterImpl((PsiTypeParameter) psiClass);
        }
        else {
            return new JavaClassImpl(psiClass);
        }
    }

    @NotNull
    @Override
    public Collection<JavaAnnotation> getAnnotations() {
        return JavaElementUtil.getAnnotations(this);
    }

    @Nullable
    @Override
    public JavaAnnotation findAnnotation(@NotNull FqName fqName) {
        return JavaElementUtil.findAnnotation(this, fqName);
    }

    @Override
    public boolean isDeprecatedInJavaDoc() {
        return getPsi().isDeprecated();
    }
}
