/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.DeclarationDescriptorVisitor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.PackageViewDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.scopes.ChainedMemberScope
import org.jetbrains.kotlin.resolve.scopes.LazyScopeAdapter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue

class LazyPackageViewDescriptorImpl(
        override val module: ModuleDescriptorImpl,
        override val fqName: FqName,
        storageManager: StorageManager
) : DeclarationDescriptorImpl(Annotations.EMPTY, fqName.shortNameOrSpecial()), PackageViewDescriptor {

    override val fragments: List<PackageFragmentDescriptor> by storageManager.createLazyValue {
        module.packageFragmentProvider.getPackageFragments(fqName)
    }

    override val memberScope: MemberScope = LazyScopeAdapter(storageManager.createLazyValue {
        if (fragments.isEmpty()) {
            MemberScope.Empty
        }
        else {
            // Packages from SubpackagesScope are got via getContributedDescriptors(DescriptorKindFilter.PACKAGES, MemberScope.ALL_NAME_FILTER)
            val scopes = fragments.map { it.getMemberScope() } + SubpackagesScope(module, fqName)
            ChainedMemberScope("package view scope for $fqName in ${module.name}", scopes)
        }
    })

    override fun getContainingDeclaration(): PackageViewDescriptor? {
        return if (fqName.isRoot) null else module.getPackage(fqName.parent())
    }

    override fun equals(other: Any?): Boolean {
        val that = other as? PackageViewDescriptor ?: return false
        return this.fqName == that.fqName && this.module == that.module
    }

    override fun hashCode(): Int {
        var result = module.hashCode()
        result = 31 * result + fqName.hashCode()
        return result
    }

    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R = visitor.visitPackageViewDescriptor(this, data)
}
