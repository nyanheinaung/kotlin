/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.AbstractTypeAliasDescriptor
import org.jetbrains.kotlin.descriptors.impl.TypeAliasConstructorDescriptor
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtTypeAlias
import org.jetbrains.kotlin.psi.psiUtil.hasActualModifier
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.storage.NotNullLazyValue
import org.jetbrains.kotlin.storage.NullableLazyValue
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue
import org.jetbrains.kotlin.types.*

class LazyTypeAliasDescriptor(
    override val storageManager: StorageManager,
    private val trace: BindingTrace,
    containingDeclaration: DeclarationDescriptor,
    annotations: Annotations,
    name: Name,
    sourceElement: SourceElement,
    visibility: Visibility
) : AbstractTypeAliasDescriptor(containingDeclaration, annotations, name, sourceElement, visibility),
    TypeAliasDescriptor {
    override val constructors: Collection<TypeAliasConstructorDescriptor> by storageManager.createLazyValue {
        getTypeAliasConstructors()
    }

    private lateinit var underlyingTypeImpl: NotNullLazyValue<SimpleType>
    private lateinit var expandedTypeImpl: NotNullLazyValue<SimpleType>
    private lateinit var defaultTypeImpl: NotNullLazyValue<SimpleType>
    private lateinit var classDescriptorImpl: NullableLazyValue<ClassDescriptor>
    private val isActual = (source.getPsi() as? KtTypeAlias)?.hasActualModifier() == true

    override val underlyingType: SimpleType get() = underlyingTypeImpl()
    override val expandedType: SimpleType get() = expandedTypeImpl()
    override val classDescriptor: ClassDescriptor? get() = classDescriptorImpl()
    override fun getDefaultType(): SimpleType = defaultTypeImpl()

    override fun isActual(): Boolean = isActual

    fun initialize(
        declaredTypeParameters: List<TypeParameterDescriptor>,
        lazyUnderlyingType: NotNullLazyValue<SimpleType>,
        lazyExpandedType: NotNullLazyValue<SimpleType>
    ) {
        super.initialize(declaredTypeParameters)
        this.underlyingTypeImpl = lazyUnderlyingType
        this.expandedTypeImpl = lazyExpandedType
        this.defaultTypeImpl = storageManager.createLazyValue { computeDefaultType() }
        this.classDescriptorImpl = storageManager.createRecursionTolerantNullableLazyValue({ computeClassDescriptor() }, null)
    }

    private fun computeClassDescriptor(): ClassDescriptor? {
        if (underlyingType.isError) return null
        val underlyingTypeDescriptor = underlyingType.constructor.declarationDescriptor
        return when (underlyingTypeDescriptor) {
            is ClassDescriptor -> underlyingTypeDescriptor
            is TypeAliasDescriptor -> underlyingTypeDescriptor.classDescriptor
            else -> null
        }
    }

    private val lazyTypeConstructorParameters =
        storageManager.createRecursionTolerantLazyValue({ this.computeConstructorTypeParameters() }, emptyList())

    fun initialize(
        declaredTypeParameters: List<TypeParameterDescriptor>,
        underlyingType: SimpleType,
        expandedType: SimpleType
    ) = initialize(
        declaredTypeParameters,
        storageManager.createLazyValue { underlyingType },
        storageManager.createLazyValue { expandedType }
    )

    override fun substitute(substitutor: TypeSubstitutor): TypeAliasDescriptor {
        if (substitutor.isEmpty) return this
        val substituted = LazyTypeAliasDescriptor(
            storageManager, trace,
            containingDeclaration, annotations, name, source, visibility
        )
        substituted.initialize(declaredTypeParameters,
                               storageManager.createLazyValue {
                                   substitutor.substitute(underlyingType, Variance.INVARIANT)!!.asSimpleType()
                               },
                               storageManager.createLazyValue {
                                   substitutor.substitute(expandedType, Variance.INVARIANT)!!.asSimpleType()
                               }
        )
        return substituted
    }

    override fun getTypeConstructorTypeParameters(): List<TypeParameterDescriptor> =
        lazyTypeConstructorParameters()

    companion object {
        @JvmStatic
        fun create(
            storageManager: StorageManager,
            trace: BindingTrace,
            containingDeclaration: DeclarationDescriptor,
            annotations: Annotations,
            name: Name,
            sourceElement: SourceElement,
            visibility: Visibility
        ): LazyTypeAliasDescriptor =
            LazyTypeAliasDescriptor(
                storageManager, trace,
                containingDeclaration, annotations, name, sourceElement, visibility
            )
    }
}
