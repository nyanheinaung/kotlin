/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.MemberScope

class IrBuiltinsPackageFragmentDescriptorImpl(
    val containingModule: ModuleDescriptor,
    override val fqName: FqName
) : IrBuiltinsPackageFragmentDescriptor {
    private val shortName = fqName.shortName()

    override fun getName(): Name = shortName

    override fun getContainingDeclaration(): ModuleDescriptor = containingModule

    override fun getMemberScope(): MemberScope = MemberScope.Empty

    override fun getOriginal(): DeclarationDescriptorWithSource = this
    override fun getSource(): SourceElement = SourceElement.NO_SOURCE
    override val annotations: Annotations = Annotations.EMPTY

    override fun <R : Any?, D : Any?> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R {
        return visitor.visitPackageFragmentDescriptor(this, data)
    }

    override fun acceptVoid(visitor: DeclarationDescriptorVisitor<Void, Void>) {
        visitor.visitPackageFragmentDescriptor(this, null)
    }

    override fun equals(other: Any?): Boolean {
        return this === other ||
                other is IrBuiltinsPackageFragmentDescriptorImpl &&
                fqName == other.fqName &&
                containingModule == other.containingModule
    }

    override fun hashCode(): Int {
        return containingModule.hashCode() * 31 + fqName.hashCode()
    }
}
