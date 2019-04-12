/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.storage.LockBasedStorageManager
import org.jetbrains.kotlin.types.*
import org.jetbrains.kotlin.types.Variance.IN_VARIANCE
import org.jetbrains.kotlin.types.Variance.OUT_VARIANCE
import org.jetbrains.kotlin.types.checker.NewCapturedTypeConstructor
import org.jetbrains.kotlin.types.model.CapturedTypeMarker
import org.jetbrains.kotlin.types.typeUtil.builtIns

interface CapturedTypeConstructor : TypeConstructor {
    val projection: TypeProjection
}

class CapturedTypeConstructorImpl(
    override val projection: TypeProjection
) : CapturedTypeConstructor {
    var newTypeConstructor: NewCapturedTypeConstructor? = null

    init {
        assert(projection.projectionKind != Variance.INVARIANT) {
            "Only nontrivial projections can be captured, not: $projection"
        }
    }

    override fun getParameters(): List<TypeParameterDescriptor> = listOf()

    override fun getSupertypes(): Collection<KotlinType> {
        val superType = if (projection.projectionKind == Variance.OUT_VARIANCE)
            projection.type
        else
            builtIns.nullableAnyType
        return listOf(superType)
    }

    override fun isFinal() = true

    override fun isDenotable() = false

    override fun getDeclarationDescriptor() = null

    override fun toString() = "CapturedTypeConstructor($projection)"

    override fun getBuiltIns(): KotlinBuiltIns = projection.type.constructor.builtIns
}

class CapturedType(
    val typeProjection: TypeProjection,
    override val constructor: CapturedTypeConstructor = CapturedTypeConstructorImpl(typeProjection),
    override val isMarkedNullable: Boolean = false,
    override val annotations: Annotations = Annotations.EMPTY
) : SimpleType(), SubtypingRepresentatives, CapturedTypeMarker {
    override val arguments: List<TypeProjection>
        get() = listOf()

    override val memberScope: MemberScope
        get() = ErrorUtils.createErrorScope(
            "No member resolution should be done on captured type, it used only during constraint system resolution", true
        )

    override val subTypeRepresentative: KotlinType
        get() = representative(OUT_VARIANCE, builtIns.nullableAnyType)

    override val superTypeRepresentative: KotlinType
        get() = representative(IN_VARIANCE, builtIns.nothingType)

    private fun representative(variance: Variance, default: KotlinType) =
        if (typeProjection.projectionKind == variance) typeProjection.type else default

    override fun sameTypeConstructor(type: KotlinType) = constructor === type.constructor

    override fun toString() = "Captured($typeProjection)" + if (isMarkedNullable) "?" else ""

    override fun makeNullableAsSpecified(newNullability: Boolean): CapturedType {
        if (newNullability == isMarkedNullable) return this
        return CapturedType(typeProjection, constructor, newNullability, annotations)
    }

    override fun replaceAnnotations(newAnnotations: Annotations): CapturedType =
        CapturedType(typeProjection, constructor, isMarkedNullable, newAnnotations)
}

fun createCapturedType(typeProjection: TypeProjection): KotlinType = CapturedType(typeProjection)

fun KotlinType.isCaptured(): Boolean = constructor is CapturedTypeConstructor

fun TypeSubstitution.wrapWithCapturingSubstitution(needApproximation: Boolean = true): TypeSubstitution =
    if (this is IndexedParametersSubstitution)
        IndexedParametersSubstitution(
            this.parameters,
            this.arguments.zip(this.parameters).map {
                it.first.createCapturedIfNeeded(it.second)
            }.toTypedArray(),
            approximateCapturedTypes = needApproximation
        )
    else
        object : DelegatedTypeSubstitution(this@wrapWithCapturingSubstitution) {
            override fun approximateContravariantCapturedTypes() = needApproximation
            override fun get(key: KotlinType) =
                super.get(key)?.createCapturedIfNeeded(key.constructor.declarationDescriptor as? TypeParameterDescriptor)
        }

private fun TypeProjection.createCapturedIfNeeded(typeParameterDescriptor: TypeParameterDescriptor?): TypeProjection {
    if (typeParameterDescriptor == null || projectionKind == Variance.INVARIANT) return this

    // Treat consistent projections as invariant
    if (typeParameterDescriptor.variance == projectionKind) {
        // TODO: Make star projection type lazy
        return if (isStarProjection)
            TypeProjectionImpl(LazyWrappedType(LockBasedStorageManager.NO_LOCKS) {
                this@createCapturedIfNeeded.type
            })
        else
            TypeProjectionImpl(this@createCapturedIfNeeded.type)
    }

    return TypeProjectionImpl(createCapturedType(this))
}
