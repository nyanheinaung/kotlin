/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiAnnotationOwner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocCommentOwner;
import com.intellij.psi.PsiMember;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.Visibility;
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation;
import org.jetbrains.kotlin.load.java.structure.JavaClass;
import org.jetbrains.kotlin.load.java.structure.JavaMember;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;

import java.util.Collection;

public abstract class JavaMemberImpl<Psi extends PsiMember> extends JavaElementImpl<Psi>
        implements JavaMember, JavaAnnotationOwnerImpl, JavaModifierListOwnerImpl {
    protected JavaMemberImpl(@NotNull Psi psiMember) {
        super(psiMember);
    }

    @Nullable
    @Override
    public PsiAnnotationOwner getAnnotationOwnerPsi() {
        return getPsi().getModifierList();
    }

    @NotNull
    @Override
    public Name getName() {
        String name = getPsi().getName();
        assert name != null && Name.isValidIdentifier(name) : "Member must have a name: " + getPsi().getText();
        return Name.identifier(name);
    }

    @NotNull
    @Override
    public JavaClass getContainingClass() {
        PsiClass psiClass = getPsi().getContainingClass();
        assert psiClass != null : "Member must have a containing class: " + getPsi();
        return new JavaClassImpl(psiClass);
    }

    @Override
    public boolean isAbstract() {
        return JavaElementUtil.isAbstract(this);
    }

    @Override
    public boolean isStatic() {
        return JavaElementUtil.isStatic(this);
    }

    @Override
    public boolean isFinal() {
        return JavaElementUtil.isFinal(this);
    }

    @NotNull
    @Override
    public Visibility getVisibility() {
        return JavaElementUtil.getVisibility(this);
    }

    @NotNull
    @Override
    public Collection<JavaAnnotation> getAnnotations() {
        return JavaElementUtil.getRegularAndExternalAnnotations(this);
    }

    @Nullable
    @Override
    public JavaAnnotation findAnnotation(@NotNull FqName fqName) {
        return JavaElementUtil.findAnnotation(this, fqName);
    }

    @Override
    public boolean isDeprecatedInJavaDoc() {
        PsiMember psi = getPsi();
        return psi instanceof PsiDocCommentOwner && ((PsiDocCommentOwner) psi).isDeprecated();
    }
}
