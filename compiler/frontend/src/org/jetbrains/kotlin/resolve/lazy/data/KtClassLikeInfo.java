/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.data;

import com.intellij.psi.PsiElement;
import kotlin.annotations.jvm.ReadOnly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.ClassKind;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.*;

import java.util.List;

public interface KtClassLikeInfo extends KtDeclarationContainer {
    @NotNull
    FqName getContainingPackageFqName();

    @Nullable
    KtModifierList getModifierList();

    @NotNull
    @ReadOnly
    List<KtObjectDeclaration> getCompanionObjects();

    // This element is used to identify resolution scope for the class
    @NotNull
    PsiElement getScopeAnchor();

    @Nullable // can be null in KtScript
    KtClassOrObject getCorrespondingClassOrObject();

    @Nullable
    KtTypeParameterList getTypeParameterList();

    @NotNull
    @ReadOnly
    List<? extends KtParameter> getPrimaryConstructorParameters();

    @NotNull
    ClassKind getClassKind();

    @NotNull
    List<KtAnnotationEntry> getDanglingAnnotations();
}
