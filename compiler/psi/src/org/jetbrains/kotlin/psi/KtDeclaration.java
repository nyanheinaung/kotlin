/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.util.ArrayFactory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.kdoc.psi.api.KDoc;

public interface KtDeclaration extends KtExpression, KtModifierListOwner {
    KtDeclaration[] EMPTY_ARRAY = new KtDeclaration[0];

    ArrayFactory<KtDeclaration> ARRAY_FACTORY = count -> count == 0 ? EMPTY_ARRAY : new KtDeclaration[count];

    @Nullable
    KDoc getDocComment();
}
