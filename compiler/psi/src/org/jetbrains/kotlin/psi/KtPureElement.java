/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * A minimal interface that {@link KtElement} implements for the purpose of code-generation that does not need the full power of PSI.
 * This interface can be easily implemented by synthetic elements to generate code for them.
 */
public interface KtPureElement {
    /**
     * Returns this or parent source element (for synthetic element declarations).
     * Use it only for the purposes of source attribution.
     */
    @NotNull
    KtElement getPsiOrParent();

    /**
     * Returns parent source element.
     */
    PsiElement getParent();

    @NotNull
    KtFile getContainingKtFile();
}
