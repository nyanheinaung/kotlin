/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi;

import com.intellij.psi.PsiNameIdentifierOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.name.Name;

public interface KtNamedDeclaration extends KtDeclaration, PsiNameIdentifierOwner, KtStatementExpression, KtNamed {
    @NotNull
    Name getNameAsSafeName();

    @Nullable
    FqName getFqName();
}
