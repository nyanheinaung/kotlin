/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.util

import org.jetbrains.kotlin.descriptors.annotations.BuiltInAnnotationDescriptor
import org.jetbrains.kotlin.renderer.*
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.isDynamic
import org.jetbrains.kotlin.types.typeUtil.builtIns

object IdeDescriptorRenderers {
    @JvmField val APPROXIMATE_FLEXIBLE_TYPES: (KotlinType) -> KotlinType = { it.approximateFlexibleTypes(preferNotNull = false) }

    @JvmField val APPROXIMATE_FLEXIBLE_TYPES_NOT_NULL: (KotlinType) -> KotlinType = { it.approximateFlexibleTypes(preferNotNull = true) }

    private fun unwrapAnonymousType(type: KotlinType): KotlinType {
        if (type.isDynamic()) return type

        val classifier = type.constructor.declarationDescriptor
        if (classifier != null && !classifier.name.isSpecial) return type

        type.constructor.supertypes.singleOrNull()?.let { return it }

        val builtIns = type.builtIns
        return if (type.isMarkedNullable)
            builtIns.nullableAnyType
        else
            builtIns.anyType
    }

    private val BASE: DescriptorRenderer = DescriptorRenderer.withOptions {
        normalizedVisibilities = true
        withDefinedIn = false
        renderDefaultVisibility = false
        overrideRenderingPolicy = OverrideRenderingPolicy.RENDER_OVERRIDE
        unitReturnType = false
        enhancedTypes = true
        modifiers = DescriptorRendererModifier.ALL
        renderUnabbreviatedType = false
        annotationArgumentsRenderingPolicy = AnnotationArgumentsRenderingPolicy.UNLESS_EMPTY
    }

    @JvmField val SOURCE_CODE: DescriptorRenderer = BASE.withOptions {
        classifierNamePolicy = ClassifierNamePolicy.SOURCE_CODE_QUALIFIED
        typeNormalizer = { APPROXIMATE_FLEXIBLE_TYPES(unwrapAnonymousType(it)) }
    }

    @JvmField val SOURCE_CODE_TYPES: DescriptorRenderer = BASE.withOptions {
        classifierNamePolicy = ClassifierNamePolicy.SOURCE_CODE_QUALIFIED
        typeNormalizer = { APPROXIMATE_FLEXIBLE_TYPES(unwrapAnonymousType(it)) }
        annotationFilter = { it !is BuiltInAnnotationDescriptor }
        parameterNamesInFunctionalTypes = false
    }

    @JvmField val SOURCE_CODE_TYPES_WITH_SHORT_NAMES: DescriptorRenderer = BASE.withOptions {
        classifierNamePolicy = ClassifierNamePolicy.SHORT
        typeNormalizer = { APPROXIMATE_FLEXIBLE_TYPES(unwrapAnonymousType(it)) }
        modifiers -= DescriptorRendererModifier.ANNOTATIONS
        parameterNamesInFunctionalTypes = false
    }

    @JvmField val SOURCE_CODE_NOT_NULL_TYPE_APPROXIMATION: DescriptorRenderer = BASE.withOptions {
        classifierNamePolicy = ClassifierNamePolicy.SOURCE_CODE_QUALIFIED
        typeNormalizer = { APPROXIMATE_FLEXIBLE_TYPES_NOT_NULL(unwrapAnonymousType(it)) }
    }

    @JvmField val SOURCE_CODE_SHORT_NAMES_NO_ANNOTATIONS: DescriptorRenderer = BASE.withOptions {
        classifierNamePolicy = ClassifierNamePolicy.SHORT
        typeNormalizer = { APPROXIMATE_FLEXIBLE_TYPES(unwrapAnonymousType(it)) }
        modifiers -= DescriptorRendererModifier.ANNOTATIONS
    }
}
