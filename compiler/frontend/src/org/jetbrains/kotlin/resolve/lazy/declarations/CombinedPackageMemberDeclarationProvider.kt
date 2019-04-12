/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtDestructuringDeclarationEntry
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter

class CombinedPackageMemberDeclarationProvider(
    val providers: Collection<PackageMemberDeclarationProvider>
) : PackageMemberDeclarationProvider {
    override fun getAllDeclaredSubPackages(nameFilter: (Name) -> Boolean) = providers.flatMap { it.getAllDeclaredSubPackages(nameFilter) }

    override fun getPackageFiles() = providers.flatMap { it.getPackageFiles() }

    override fun containsFile(file: KtFile) = providers.any { it.containsFile(file) }

    override fun getDeclarations(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean) =
        providers.flatMap { it.getDeclarations(kindFilter, nameFilter) }

    override fun getFunctionDeclarations(name: Name) = providers.flatMap { it.getFunctionDeclarations(name) }

    override fun getPropertyDeclarations(name: Name) = providers.flatMap { it.getPropertyDeclarations(name) }

    override fun getDestructuringDeclarationsEntries(name: Name): Collection<KtDestructuringDeclarationEntry> {
        return providers.flatMap { it.getDestructuringDeclarationsEntries(name) }
    }

    override fun getClassOrObjectDeclarations(name: Name) = providers.flatMap { it.getClassOrObjectDeclarations(name) }

    override fun getScriptDeclarations(name: Name) = providers.flatMap { it.getScriptDeclarations(name) }

    override fun getTypeAliasDeclarations(name: Name) = providers.flatMap { it.getTypeAliasDeclarations(name) }

    override fun getDeclarationNames(): Set<Name> = providers.flatMapTo(HashSet()) { it.getDeclarationNames() }
}
