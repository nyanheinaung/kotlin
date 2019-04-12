/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtFile

interface PackageMemberDeclarationProvider : DeclarationProvider {
    fun getAllDeclaredSubPackages(nameFilter: (Name) -> Boolean): Collection<FqName>

    fun getPackageFiles(): Collection<KtFile>

    fun containsFile(file: KtFile): Boolean
}
