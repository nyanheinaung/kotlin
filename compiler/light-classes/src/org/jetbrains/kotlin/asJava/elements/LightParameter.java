/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.elements;

import com.intellij.lang.Language;
import com.intellij.psi.*;
import org.jetbrains.annotations.NotNull;

// Based on com.intellij.psi.impl.light.LightParameter
public class LightParameter extends LightVariableBuilder implements PsiParameter {
    public static final LightParameter[] EMPTY_ARRAY = new LightParameter[0];

    private final String myName;
    private final KtLightMethod myDeclarationScope;
    private final boolean myVarArgs;

    public LightParameter(@NotNull String name, @NotNull PsiType type, @NotNull KtLightMethod declarationScope, Language language) {
        this(name, type, declarationScope, language, type instanceof PsiEllipsisType);
    }

    public LightParameter(@NotNull String name, @NotNull PsiType type, @NotNull KtLightMethod declarationScope, Language language, boolean isVarArgs) {
        super(declarationScope.getManager(), name, type, language);
        myName = name;
        myDeclarationScope = declarationScope;
        myVarArgs = isVarArgs;
    }

    @NotNull
    @Override
    public KtLightMethod getDeclarationScope() {
        return myDeclarationScope;
    }

    public KtLightMethod getMethod() {
        return myDeclarationScope;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof JavaElementVisitor) {
            ((JavaElementVisitor)visitor).visitParameter(this);
        }
    }

    public String toString() {
        return "Light Parameter";
    }

    @Override
    public boolean isVarArgs() {
        return myVarArgs;
    }

    @Override
    @NotNull
    public String getName() {
        return myName;
    }
}
