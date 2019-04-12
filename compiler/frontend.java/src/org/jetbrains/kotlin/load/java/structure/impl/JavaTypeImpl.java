/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation;
import org.jetbrains.kotlin.load.java.structure.JavaType;
import org.jetbrains.kotlin.name.FqName;

import java.util.Collection;

public abstract class JavaTypeImpl<Psi extends PsiType> implements JavaType, JavaAnnotationOwnerImpl {
    private final Psi psiType;

    public JavaTypeImpl(@NotNull Psi psiType) {
        this.psiType = psiType;
    }

    @NotNull
    public Psi getPsi() {
        return psiType;
    }

    @Nullable
    @Override
    public PsiAnnotationOwner getAnnotationOwnerPsi() {
        return getPsi();
    }

    @NotNull
    public static JavaTypeImpl<?> create(@NotNull PsiType psiType) {
        return psiType.accept(new PsiTypeVisitor<JavaTypeImpl<?>>() {
            @Nullable
            @Override
            public JavaTypeImpl<?> visitType(@NotNull PsiType type) {
                throw new UnsupportedOperationException("Unsupported PsiType: " + type);
            }

            @Nullable
            @Override
            public JavaTypeImpl<?> visitPrimitiveType(@NotNull PsiPrimitiveType primitiveType) {
                return new JavaPrimitiveTypeImpl(primitiveType);
            }

            @Nullable
            @Override
            public JavaTypeImpl<?> visitArrayType(@NotNull PsiArrayType arrayType) {
                return new JavaArrayTypeImpl(arrayType);
            }

            @Nullable
            @Override
            public JavaTypeImpl<?> visitClassType(@NotNull PsiClassType classType) {
                return new JavaClassifierTypeImpl(classType);
            }

            @Nullable
            @Override
            public JavaTypeImpl<?> visitWildcardType(@NotNull PsiWildcardType wildcardType) {
                return new JavaWildcardTypeImpl(wildcardType);
            }
        });
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
        return false;
    }

    @Override
    public int hashCode() {
        return getPsi().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JavaTypeImpl && getPsi().equals(((JavaTypeImpl) obj).getPsi());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getPsi();
    }
}
