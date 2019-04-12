/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure.impl;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.load.java.structure.JavaElement;

public abstract class JavaElementImpl<Psi extends PsiElement> implements JavaElement {
    private final Psi psiElement;

    protected JavaElementImpl(@NotNull Psi psiElement) {
        this.psiElement = psiElement;
    }

    @NotNull
    public Psi getPsi() {
        return psiElement;
    }

    @Override
    public int hashCode() {
        return getPsi().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof JavaElementImpl && getPsi().equals(((JavaElementImpl) obj).getPsi());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + getPsi();
    }
}
