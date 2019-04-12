/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.impl

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.*

abstract class AbstractTypeAliasDescriptor(
    containingDeclaration: DeclarationDescriptor,
    annotations: Annotations,
    name: Name,
    sourceElement: SourceElement,
    private val visibilityImpl: Visibility
) : DeclarationDescriptorNonRootImpl(containingDeclaration, annotations, name, sourceElement),
    TypeAliasDescriptor {

    protected abstract val storageManager: StorageManager

    // TODO kotlinize some interfaces
    private lateinit var declaredTypeParametersImpl: List<TypeParameterDescriptor>

    fun initialize(declaredTypeParameters: List<TypeParameterDescriptor>) {
        this.declaredTypeParametersImpl = declaredTypeParameters
    }

    override fun <R, D> accept(visitor: DeclarationDescriptorVisitor<R, D>, data: D): R =
        visitor.visitTypeAliasDescriptor(this, data)

    override fun isInner(): Boolean =
        // NB: it's ok to use underlyingType here, since referenced inner type aliases also capture type parameters.
        // Using expandedType looks "proper", but in fact will cause a recursion in expandedType resolution,
        // which will silently produce wrong result.
        TypeUtils.contains(underlyingType) { type ->
            !type.isError && run {
                val constructorDescriptor = type.constructor.declarationDescriptor
                constructorDescriptor is TypeParameterDescriptor &&
                        constructorDescriptor.containingDeclaration != this@AbstractTypeAliasDescriptor
            }
        }


    fun getTypeAliasConstructors(): Collection<TypeAliasConstructorDescriptor> {
        val classDescriptor = this.classDescriptor ?: return emptyList()

        return classDescriptor.constructors.mapNotNull {
            TypeAliasConstructorDescriptorImpl.createIfAvailable(storageManager, this, it)
        }
    }

    override fun getDeclaredTypeParameters(): List<TypeParameterDescriptor> =
        declaredTypeParametersImpl

    override fun getModality() = Modality.FINAL

    override fun getVisibility() = visibilityImpl

    override fun isExpect(): Boolean = false

    override fun isActual(): Boolean = false

    override fun isExternal() = false

    override fun getTypeConstructor(): TypeConstructor =
        typeConstructor

    override fun toString(): String = "typealias ${name.asString()}"

    override fun getOriginal(): TypeAliasDescriptor = super.getOriginal() as TypeAliasDescriptor

    protected abstract fun getTypeConstructorTypeParameters(): List<TypeParameterDescriptor>

    protected fun computeDefaultType(): SimpleType =
        TypeUtils.makeUnsubstitutedType(this, classDescriptor?.unsubstitutedMemberScope ?: MemberScope.Empty)

    private val typeConstructor = object : TypeConstructor {
        override fun getDeclarationDescriptor(): TypeAliasDescriptor =
            this@AbstractTypeAliasDescriptor

        override fun getParameters(): List<TypeParameterDescriptor> =
            getTypeConstructorTypeParameters()

        override fun getSupertypes(): Collection<KotlinType> =
            declarationDescriptor.underlyingType.constructor.supertypes

        override fun isFinal(): Boolean =
            declarationDescriptor.underlyingType.constructor.isFinal

        override fun isDenotable(): Boolean =
            true

        override fun getBuiltIns(): KotlinBuiltIns =
            declarationDescriptor.builtIns

        override fun toString(): String = "[typealias ${declarationDescriptor.name.asString()}]"
    }
}
