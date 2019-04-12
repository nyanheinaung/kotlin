/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.elements;

import com.intellij.lang.Language;
import com.intellij.psi.*;
import com.intellij.psi.impl.light.AbstractLightClass;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.asJava.LightClassUtilsKt;
import org.jetbrains.kotlin.asJava.classes.KtLightClass;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.kotlin.psi.KtTypeParameter;
import org.jetbrains.kotlin.psi.KtTypeParameterListOwner;

import java.util.List;

public class KtLightTypeParameter
        extends AbstractLightClass implements PsiTypeParameter, KtLightDeclaration<KtTypeParameter, PsiTypeParameter> {
    private final PsiTypeParameterListOwner owner;
    private final int index;
    private final String name;

    public KtLightTypeParameter(
            @NotNull PsiTypeParameterListOwner owner,
            int index,
            @NotNull String name) {
        super(owner.getManager(), KotlinLanguage.INSTANCE);
        this.owner = owner;
        this.index = index;
        this.name = name;
    }

    @NotNull
    @Override
    public PsiTypeParameter getClsDelegate() {
        return getOwnerDelegate().getTypeParameters()[index];
    }

    @NotNull
    @Override
    public PsiClass getDelegate() {
        return getClsDelegate();
    }

    @NotNull
    @Override
    public KtTypeParameter getKotlinOrigin() {
        KtTypeParameterListOwner jetOwner = (KtTypeParameterListOwner) LightClassUtilsKt.getUnwrapped(owner);
        assert (jetOwner != null) : "Invalid type parameter owner: " + owner;

        return jetOwner.getTypeParameters().get(index);
    }

    @NotNull
    private PsiTypeParameterListOwner getOwnerDelegate() {
        if (owner instanceof KtLightClass) return ((KtLightClass) owner).getClsDelegate();
        if (owner instanceof KtLightMethod) return ((KtLightMethod) owner).getClsDelegate();
        return owner;
    }

    @NotNull
    @Override
    public PsiElement copy() {
        return new KtLightTypeParameter(owner, index, name);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof JavaElementVisitor) {
            ((JavaElementVisitor) visitor).visitTypeParameter(this);
        }
        else {
            super.accept(visitor);
        }
    }

    @Override
    public String getText() {
        return "";
    }

    @Nullable
    @Override
    public String getName() {
        return name;
    }

    @Override
    public PsiTypeParameterListOwner getOwner() {
        return owner;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @NotNull
    @Override
    public PsiAnnotation[] getAnnotations() {
        return getClsDelegate().getAnnotations();
    }

    @NotNull
    @Override
    public PsiAnnotation[] getApplicableAnnotations() {
        return getClsDelegate().getApplicableAnnotations();
    }

    @Override
    public PsiAnnotation findAnnotation(@NotNull String qualifiedName) {
        return getClsDelegate().findAnnotation(qualifiedName);
    }

    @NotNull
    @Override
    public PsiAnnotation addAnnotation(@NotNull String qualifiedName) {
        return getClsDelegate().addAnnotation(qualifiedName);
    }

    @Override
    public String toString() {
        return "KotlinLightTypeParameter:" + getName();
    }

    @NotNull
    @Override
    public PsiElement getNavigationElement() {
        return getKotlinOrigin();
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return KotlinLanguage.INSTANCE;
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return getKotlinOrigin().getUseScope();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        return obj instanceof KtLightTypeParameter && getKotlinOrigin().equals(((KtLightTypeParameter) obj).getKotlinOrigin());
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        if (another instanceof PsiTypeParameter) {
            PsiTypeParameter anotherTypeParameter = (PsiTypeParameter) another;
            PsiTypeParameterListOwner owner = getOwner();
            if (owner != null) {
                return owner.isEquivalentTo(anotherTypeParameter.getOwner()) && getIndex() == anotherTypeParameter.getIndex();
            }
        }
        return false;
    }

    @Nullable
    @Override
    public List<KtLightAbstractAnnotation> getGivenAnnotations() {
        return null;
    }
}
