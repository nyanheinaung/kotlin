/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface KtTypeElement extends KtElement {
    KtTypeElement[] EMPTY_ARRAY = new KtTypeElement[0];

    ArrayFactory<KtTypeElement> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new KtTypeElement[count];

    // may contain null
    @NotNull
    List<KtTypeReference> getTypeArgumentsAsTypes();
}
