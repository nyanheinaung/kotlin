/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kdoc.parser;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinLanguage;

import java.lang.reflect.Constructor;

public class KDocElementType extends IElementType {
    private final Constructor<? extends PsiElement> psiFactory;

    public KDocElementType(String debugName, @NotNull Class<? extends PsiElement> psiClass) {
        super(debugName, KotlinLanguage.INSTANCE);
        try {
            psiFactory = psiClass.getConstructor(ASTNode.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Must have a constructor with ASTNode");
        }
    }

    public PsiElement createPsi(ASTNode node) {
        assert node.getElementType() == this;

        try {
            return psiFactory.newInstance(node);
        } catch (Exception e) {
            throw new RuntimeException("Error creating psi element for node", e);
        }
    }
}
