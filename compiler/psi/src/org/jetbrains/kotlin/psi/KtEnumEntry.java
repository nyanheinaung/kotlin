/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiEnumConstant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.stubs.KotlinClassStub;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.Collections;
import java.util.List;

public class KtEnumEntry extends KtClass {
    public KtEnumEntry(@NotNull ASTNode node) {
        super(node);
    }

    public KtEnumEntry(@NotNull KotlinClassStub stub) {
        super(stub);
    }

    @NotNull
    @Override
    public List<KtSuperTypeListEntry> getSuperTypeListEntries() {
        KtInitializerList initializerList = getInitializerList();
        if (initializerList == null) {
            return Collections.emptyList();
        }
        return initializerList.getInitializers();
    }

    public boolean hasInitializer() {
        return !getSuperTypeListEntries().isEmpty();
    }

    @Nullable
    public KtInitializerList getInitializerList() {
        return getStubOrPsiChild(KtStubElementTypes.INITIALIZER_LIST);
    }

    @Override
    public <R, D> R accept(@NotNull KtVisitor<R, D> visitor, D data) {
        return visitor.visitEnumEntry(this, data);
    }

    @Override
    public boolean isEquivalentTo(@Nullable PsiElement another) {
        if (another instanceof PsiEnumConstant) {
            PsiEnumConstant enumConstant = (PsiEnumConstant) another;
            PsiClass containingClass = enumConstant.getContainingClass();
            if (containingClass != null) {
                String containingClassQName = containingClass.getQualifiedName();
                if (containingClassQName != null && enumConstant.getName() != null) {
                    String theirFQName = containingClassQName + "." + enumConstant.getName();
                    if (theirFQName.equals(getQualifiedName())) {
                        return true;
                    }
                }
            }
        }
        return super.isEquivalentTo(another);
    }
}
