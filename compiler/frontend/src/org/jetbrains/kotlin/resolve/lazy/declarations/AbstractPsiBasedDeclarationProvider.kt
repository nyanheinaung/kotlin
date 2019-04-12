/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import com.google.common.collect.ArrayListMultimap
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.psi.psiUtil.safeNameForLazyResolve
import org.jetbrains.kotlin.resolve.lazy.data.KtClassInfoUtil
import org.jetbrains.kotlin.resolve.lazy.data.KtClassOrObjectInfo
import org.jetbrains.kotlin.resolve.lazy.data.KtScriptInfo
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.storage.StorageManager
import java.util.*

abstract class AbstractPsiBasedDeclarationProvider(storageManager: StorageManager) : DeclarationProvider {

    protected class Index {
        // This mutable state is only modified under inside the computable
        val allDeclarations = ArrayList<KtDeclaration>()
        val functions = ArrayListMultimap.create<Name, KtNamedFunction>()
        val properties = ArrayListMultimap.create<Name, KtProperty>()
        val classesAndObjects = ArrayListMultimap.create<Name, KtClassOrObjectInfo<*>>() // order matters here
        val scripts = ArrayListMultimap.create<Name, KtScriptInfo>()
        val typeAliases = ArrayListMultimap.create<Name, KtTypeAlias>()
        val destructuringDeclarationsEntries = ArrayListMultimap.create<Name, KtDestructuringDeclarationEntry>()
        val names = hashSetOf<Name>()

        fun putToIndex(declaration: KtDeclaration) {
            if (declaration is KtAnonymousInitializer || declaration is KtSecondaryConstructor) return

            allDeclarations.add(declaration)
            when (declaration) {
                is KtNamedFunction ->
                    functions.put(declaration.safeNameForLazyResolve(), declaration)
                is KtProperty ->
                    properties.put(declaration.safeNameForLazyResolve(), declaration)
                is KtTypeAlias ->
                    typeAliases.put(declaration.nameAsName.safeNameForLazyResolve(), declaration)
                is KtClassOrObject ->
                    classesAndObjects.put(declaration.nameAsName.safeNameForLazyResolve(), KtClassInfoUtil.createClassOrObjectInfo(declaration))
                is KtScript ->
                    scripts.put(KtScriptInfo(declaration).script.nameAsName, KtScriptInfo(declaration))
                is KtDestructuringDeclaration -> {
                    for (entry in declaration.entries) {
                        val name = entry.nameAsName.safeNameForLazyResolve()
                        destructuringDeclarationsEntries.put(name, entry)
                        names.add(name)
                    }
                }
                is KtParameter -> {
                    // Do nothing, just put it into allDeclarations is enough
                }
                else -> throw IllegalArgumentException("Unknown declaration: " + declaration)
            }

            when (declaration) {
                is KtNamedDeclaration -> names.add(declaration.safeNameForLazyResolve())
            }
        }

        override fun toString() = "allDeclarations: " + allDeclarations.mapNotNull { it.name }
    }

    override fun getDeclarationNames() = index().names

    private val index = storageManager.createLazyValue<Index> {
        val index = Index()
        doCreateIndex(index)
        index
    }

    protected abstract fun doCreateIndex(index: Index)

    internal fun toInfoString() = toString() + ": " + index().toString()

    override fun getDeclarations(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean): List<KtDeclaration> =
        index().allDeclarations

    override fun getFunctionDeclarations(name: Name): List<KtNamedFunction> = index().functions[name.safeNameForLazyResolve()].toList()

    override fun getPropertyDeclarations(name: Name): List<KtProperty> = index().properties[name.safeNameForLazyResolve()].toList()

    override fun getDestructuringDeclarationsEntries(name: Name): Collection<KtDestructuringDeclarationEntry> =
        index().destructuringDeclarationsEntries[name.safeNameForLazyResolve()].toList()

    override fun getClassOrObjectDeclarations(name: Name): Collection<KtClassOrObjectInfo<*>> =
        index().classesAndObjects[name.safeNameForLazyResolve()]

    override fun getScriptDeclarations(name: Name): MutableList<KtScriptInfo> =
        index().scripts[name.safeNameForLazyResolve()]

    override fun getTypeAliasDeclarations(name: Name): Collection<KtTypeAlias> = index().typeAliases[name.safeNameForLazyResolve()]
}
