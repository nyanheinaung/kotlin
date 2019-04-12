/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.psi.KtFile;
import org.jetbrains.kotlin.storage.MemoizedFunctionToNullable;
import org.jetbrains.kotlin.storage.StorageManager;

public abstract class AbstractDeclarationProviderFactory implements DeclarationProviderFactory {
    private final MemoizedFunctionToNullable<FqName, PackageMemberDeclarationProvider> packageDeclarationProviders;

    public AbstractDeclarationProviderFactory(@NotNull StorageManager storageManager) {
        this.packageDeclarationProviders =
                storageManager.createMemoizedFunctionWithNullableValues(this::createPackageMemberDeclarationProvider);
    }

    @Nullable
    protected abstract PackageMemberDeclarationProvider createPackageMemberDeclarationProvider(@NotNull FqName name);

    public abstract boolean packageExists(@NotNull FqName fqName);

    @Override
    public PackageMemberDeclarationProvider getPackageMemberDeclarationProvider(@NotNull FqName packageFqName) {
        if (!packageExists(packageFqName)) return null;
        return packageDeclarationProviders.invoke(packageFqName);
    }

    @Override
    public void diagnoseMissingPackageFragment(@NotNull FqName fqName, @Nullable KtFile file) {
        String message = "Cannot find package fragment " + fqName;
        if (file != null) {
            message += "\nvFile = " + file.getVirtualFilePath() + ", file package = '" + file.getPackageFqName() + "'";
        }
        throw new IllegalStateException(message);
    }
}
