/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.resolve.lazy.data.KtClassLikeInfo;
import org.jetbrains.kotlin.storage.LockBasedStorageManager;

import java.util.Collections;

public interface DeclarationProviderFactory {
    DeclarationProviderFactory EMPTY = new FileBasedDeclarationProviderFactory(LockBasedStorageManager.NO_LOCKS, Collections.emptyList());

    @NotNull
    ClassMemberDeclarationProvider getClassMemberDeclarationProvider(@NotNull KtClassLikeInfo classLikeInfo);

    @Nullable
    PackageMemberDeclarationProvider getPackageMemberDeclarationProvider(@NotNull FqName packageFqName);

    void diagnoseMissingPackageFragment(@NotNull FqName fqName, @Nullable KtFile file);
}
