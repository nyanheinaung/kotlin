/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.storage.StorageManager

class FileBasedPackageMemberDeclarationProvider(
    storageManager: StorageManager,
    private val fqName: FqName,
    private val factory: FileBasedDeclarationProviderFactory,
    private val packageFiles: Collection<KtFile>
) : AbstractPsiBasedDeclarationProvider(storageManager), PackageMemberDeclarationProvider {

    private val allDeclaredSubPackages = storageManager.createLazyValue<Collection<FqName>> {
        factory.getAllDeclaredSubPackagesOf(fqName)
    }

    override fun doCreateIndex(index: AbstractPsiBasedDeclarationProvider.Index) {
        for (file in packageFiles) {
            for (declaration in file.declarations) {
                assert(fqName == file.packageFqName) { "Files declaration utils contains file with invalid package" }
                index.putToIndex(declaration)
            }
        }
    }

    override fun getAllDeclaredSubPackages(nameFilter: (Name) -> Boolean): Collection<FqName> = allDeclaredSubPackages()

    override fun getPackageFiles() = packageFiles

    override fun containsFile(file: KtFile) = file in packageFiles

    override fun toString() = "Declarations for package $fqName with files ${packageFiles.map { it.name }} " +
            "with declarations inside ${packageFiles.flatMap { it.declarations }.map { it.name ?: "???" }}"
}
