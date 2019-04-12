/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.lazy.descriptors

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.impl.AbstractLazyTypeParameterDescriptor
import org.jetbrains.kotlin.load.java.components.TypeUsage
import org.jetbrains.kotlin.load.java.lazy.LazyJavaAnnotations
import org.jetbrains.kotlin.load.java.lazy.LazyJavaResolverContext
import org.jetbrains.kotlin.load.java.lazy.types.toAttributes
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.Variance

class LazyJavaTypeParameterDescriptor(
    private val c: LazyJavaResolverContext,
    val javaTypeParameter: JavaTypeParameter,
    index: Int,
    containingDeclaration: DeclarationDescriptor
) : AbstractLazyTypeParameterDescriptor(
    c.storageManager,
    containingDeclaration,
    javaTypeParameter.name,
    Variance.INVARIANT,
    /* isReified = */ false,
    index,
    SourceElement.NO_SOURCE, c.components.supertypeLoopChecker
) {
    override val annotations = LazyJavaAnnotations(c, javaTypeParameter)

    override fun resolveUpperBounds(): List<KotlinType> {
        val bounds = javaTypeParameter.upperBounds
        if (bounds.isEmpty()) {
            return listOf(
                KotlinTypeFactory.flexibleType(
                    c.module.builtIns.anyType,
                    c.module.builtIns.nullableAnyType
                )
            )
        }
        return bounds.map {
            c.typeResolver.transformJavaType(it, TypeUsage.COMMON.toAttributes(upperBoundForTypeParameter = this))
        }
    }

    override fun reportSupertypeLoopError(type: KotlinType) {
        // Do nothing
    }
}
