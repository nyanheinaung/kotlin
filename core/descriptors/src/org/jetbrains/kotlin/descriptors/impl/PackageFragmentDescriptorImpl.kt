/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.descriptors.DeclarationDescriptorVisitor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.FqName

abstract class PackageFragmentDescriptorImpl(
        module: ModuleDescriptor,
        final override val fqName: FqName
) : DeclarationDescriptorNonRootImpl(module, Annotations.EMPTY, fqName.shortNameOrSpecial(), SourceElement.NO_SOURCE),
        PackageFragmentDescriptor {
    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R =
            visitor.visitPackageFragmentDescriptor(this, data)

    override fun getContainingDeclaration(): ModuleDescriptor {
        return super.getContainingDeclaration() as ModuleDescriptor
    }

    override fun getSource(): SourceElement {
        return SourceElement.NO_SOURCE
    }

    override fun toString(): String = "package $fqName"
}
