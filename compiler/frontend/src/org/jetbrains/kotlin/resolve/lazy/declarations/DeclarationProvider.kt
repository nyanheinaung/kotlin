/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.lazy.data.KtClassOrObjectInfo
import org.jetbrains.kotlin.resolve.lazy.data.KtScriptInfo
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter

interface DeclarationProvider {
    fun getDeclarations(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): List<KtDeclaration>

    fun getFunctionDeclarations(name: Name): Collection<KtNamedFunction>

    fun getPropertyDeclarations(name: Name): Collection<KtProperty>

    fun getDestructuringDeclarationsEntries(name: Name): Collection<KtDestructuringDeclarationEntry>

    fun getClassOrObjectDeclarations(name: Name): Collection<KtClassOrObjectInfo<*>>

    fun getScriptDeclarations(name: Name): Collection<KtScriptInfo>

    fun getTypeAliasDeclarations(name: Name): Collection<KtTypeAlias>

    fun getDeclarationNames(): Set<Name>
}
