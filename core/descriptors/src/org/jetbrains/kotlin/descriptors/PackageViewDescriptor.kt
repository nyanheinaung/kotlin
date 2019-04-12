/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors

import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.scopes.MemberScope

interface PackageViewDescriptor : DeclarationDescriptor {
    override fun getContainingDeclaration(): PackageViewDescriptor?

    val fqName: FqName

    val memberScope: MemberScope

    val module: ModuleDescriptor

    val fragments: List<PackageFragmentDescriptor>

    fun isEmpty(): Boolean = fragments.isEmpty()
}
