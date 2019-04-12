/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter
import org.jetbrains.kotlin.name.Name
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.TypeVariable

class ReflectJavaTypeParameter(
    val typeVariable: TypeVariable<*>
) : ReflectJavaElement(), JavaTypeParameter, ReflectJavaAnnotationOwner {
    override val upperBounds: List<ReflectJavaClassifierType>
        get() {
            val bounds = typeVariable.bounds.map(::ReflectJavaClassifierType)
            if (bounds.singleOrNull()?.reflectType == Any::class.java) return emptyList()
            return bounds
        }

    override val element: AnnotatedElement?
        // TypeVariable is AnnotatedElement only in JDK8
        get() = typeVariable as? AnnotatedElement

    override val name: Name
        get() = Name.identifier(typeVariable.name)

    override fun equals(other: Any?) = other is ReflectJavaTypeParameter && typeVariable == other.typeVariable

    override fun hashCode() = typeVariable.hashCode()

    override fun toString() = this::class.java.name + ": " + typeVariable
}
