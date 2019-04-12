/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.VariableDescriptorImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeSubstitutor

interface IrTemporaryVariableDescriptor : VariableDescriptor

class IrTemporaryVariableDescriptorImpl(
    containingDeclaration: DeclarationDescriptor,
    name: Name,
    outType: KotlinType,
    private val isMutable: Boolean = false
) : VariableDescriptorImpl(containingDeclaration, Annotations.EMPTY, name, outType, SourceElement.NO_SOURCE),
    IrTemporaryVariableDescriptor {
    override fun getCompileTimeInitializer(): ConstantValue<*>? = null

    override fun getVisibility(): Visibility = Visibilities.LOCAL

    override fun substitute(substitutor: TypeSubstitutor): VariableDescriptor {
        throw UnsupportedOperationException("Temporary variable descriptor shouldn't be substituted (so far): $this")
    }

    override fun isVar(): Boolean = isMutable

    override fun isLateInit(): Boolean = false

    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R =
        visitor.visitVariableDescriptor(this, data)
}