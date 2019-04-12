/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.codeInsight.ExternalAnnotationsManager;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationArgument;
import org.jetbrains.kotlin.load.java.structure.JavaClass;
import org.jetbrains.kotlin.name.ClassId;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;

import java.util.Collection;

import static org.jetbrains.kotlin.load.java.structure.impl.JavaElementCollectionFromPsiArrayUtil.namedAnnotationArguments;

public class JavaAnnotationImpl extends JavaElementImpl<PsiAnnotation> implements JavaAnnotation {
    public JavaAnnotationImpl(@NotNull PsiAnnotation psiAnnotation) {
        super(psiAnnotation);
    }

    @Override
    @NotNull
    public Collection<JavaAnnotationArgument> getArguments() {
        return namedAnnotationArguments(getPsi().getParameterList().getAttributes());
    }

    @Override
    @Nullable
    public ClassId getClassId() {
        PsiClass resolved = resolvePsi();
        if (resolved != null) return computeClassId(resolved);

        // External annotations do not have PSI behind them,
        // so we can only heuristically reconstruct annotation class ids from qualified names
        String qualifiedName = getPsi().getQualifiedName();
        if (qualifiedName != null) return ClassId.topLevel(new FqName(qualifiedName));

        return null;
    }

    @Nullable
    @Override
    public JavaClass resolve() {
        PsiClass resolved = resolvePsi();
        return resolved == null ? null : new JavaClassImpl(resolved);
    }

    @Nullable
    private static ClassId computeClassId(@NotNull PsiClass psiClass) {
        PsiClass container = psiClass.getContainingClass();
        if (container != null) {
            ClassId parentClassId = computeClassId(container);
            String name = psiClass.getName();
            return parentClassId == null || name == null ? null : parentClassId.createNestedClassId(Name.identifier(name));
        }

        String fqName = psiClass.getQualifiedName();
        return fqName == null ? null : ClassId.topLevel(new FqName(fqName));
    }

    @Nullable
    private PsiClass resolvePsi() {
        PsiJavaCodeReferenceElement referenceElement = getPsi().getNameReferenceElement();
        if (referenceElement == null) return null;

        PsiElement resolved = referenceElement.resolve();
        return resolved instanceof PsiClass ? (PsiClass) resolved : null;
    }

    @Override
    public boolean isIdeExternalAnnotation() {
        PsiAnnotation psi = getPsi();
        ExternalAnnotationsManager externalAnnotationManager = ExternalAnnotationsManager.getInstance(psi.getProject());
        return externalAnnotationManager.isExternalAnnotation(psi);
    }
}
