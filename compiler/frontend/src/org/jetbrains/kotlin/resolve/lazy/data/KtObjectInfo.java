/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.descriptors.ClassKind;
import org.jetbrains.kotlin.psi.KtClassOrObject;
import org.jetbrains.kotlin.psi.KtObjectDeclaration;
import org.jetbrains.kotlin.psi.KtStubbedPsiUtil;
import org.jetbrains.kotlin.psi.KtTypeParameterList;

public class KtObjectInfo extends KtClassOrObjectInfo<KtObjectDeclaration> {
    @NotNull
    private final ClassKind kind;

    protected KtObjectInfo(@NotNull KtObjectDeclaration element) {
        super(element);
        this.kind = element.isObjectLiteral() ? ClassKind.CLASS : ClassKind.OBJECT;
    }

    @Nullable
    @Override
    public KtTypeParameterList getTypeParameterList() {
        return element.getTypeParameterList();
    }

    @NotNull
    @Override
    public ClassKind getClassKind() {
        return kind;
    }

    public boolean isCompanionObject() {
        return element.isCompanion() &&
               KtStubbedPsiUtil.getContainingDeclaration(element) instanceof KtClassOrObject;
    }
}
