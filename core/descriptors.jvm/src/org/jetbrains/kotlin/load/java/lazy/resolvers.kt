/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.lazy

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.load.java.lazy.descriptors.LazyJavaTypeParameterDescriptor
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameterListOwner
import org.jetbrains.kotlin.utils.mapToIndex

interface TypeParameterResolver {
    object EMPTY : TypeParameterResolver {
        override fun resolveTypeParameter(javaTypeParameter: JavaTypeParameter): TypeParameterDescriptor? = null
    }

    fun resolveTypeParameter(javaTypeParameter: JavaTypeParameter): TypeParameterDescriptor?
}

class LazyJavaTypeParameterResolver(
    private val c: LazyJavaResolverContext,
    private val containingDeclaration: DeclarationDescriptor,
    typeParameterOwner: JavaTypeParameterListOwner,
    private val typeParametersIndexOffset: Int
) : TypeParameterResolver {
    private val typeParameters: Map<JavaTypeParameter, Int> = typeParameterOwner.typeParameters.mapToIndex()

    private val resolve = c.storageManager.createMemoizedFunctionWithNullableValues { typeParameter: JavaTypeParameter ->
        typeParameters[typeParameter]?.let { index ->
            LazyJavaTypeParameterDescriptor(c.child(this), typeParameter, typeParametersIndexOffset + index, containingDeclaration)
        }
    }

    override fun resolveTypeParameter(javaTypeParameter: JavaTypeParameter): TypeParameterDescriptor? {
        return resolve(javaTypeParameter) ?: c.typeParameterResolver.resolveTypeParameter(javaTypeParameter)
    }
}
