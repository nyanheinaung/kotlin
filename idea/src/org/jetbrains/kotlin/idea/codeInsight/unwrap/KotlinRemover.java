/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.codeInsight.unwrap;

import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;

public abstract class KotlinRemover extends KotlinUnwrapRemoveBase {
    public KotlinRemover(String key) {
        super(key);
    }

    @Override
    protected void doUnwrap(PsiElement element, Context context) throws IncorrectOperationException {
        context.delete(element);
    }
}
