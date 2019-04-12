/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.kotlin.psi.KtElement;
import org.jetbrains.kotlin.psi.KtElementImpl;

import java.lang.reflect.Constructor;

public class KtNodeType extends IElementType {
    private final Constructor<? extends KtElement> myPsiFactory;

    public KtNodeType(@NotNull @NonNls String debugName, Class<? extends KtElement> psiClass) {
        super(debugName, KotlinLanguage.INSTANCE);
        try {
            myPsiFactory = psiClass != null ? psiClass.getConstructor(ASTNode.class) : null;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Must have a constructor with ASTNode");
        }
    }

    public KtElement createPsi(ASTNode node) {
        assert node.getElementType() == this;

        try {
            if (myPsiFactory == null) {
                return new KtElementImpl(node);
            }
            return myPsiFactory.newInstance(node);
        } catch (Exception e) {
            throw new RuntimeException("Error creating psi element for node", e);
        }
    }

    public static class KtLeftBoundNodeType extends KtNodeType {
        public KtLeftBoundNodeType(@NotNull @NonNls String debugName, Class<? extends KtElement> psiClass) {
            super(debugName, psiClass);
        }

        @Override
        public boolean isLeftBound() {
            return true;
        }
    }
}
