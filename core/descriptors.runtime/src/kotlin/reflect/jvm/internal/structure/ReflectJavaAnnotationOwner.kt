/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaAnnotationOwner
import org.jetbrains.kotlin.name.FqName
import java.lang.reflect.AnnotatedElement

interface ReflectJavaAnnotationOwner : JavaAnnotationOwner {
    val element: AnnotatedElement?

    override val annotations: List<ReflectJavaAnnotation>
        get() = element?.declaredAnnotations?.getAnnotations() ?: emptyList()

    override fun findAnnotation(fqName: FqName) =
        element?.declaredAnnotations?.findAnnotation(fqName)

    override val isDeprecatedInJavaDoc: Boolean
        get() = false
}

fun Array<Annotation>.getAnnotations(): List<ReflectJavaAnnotation> {
    return map(::ReflectJavaAnnotation)
}

fun Array<Annotation>.findAnnotation(fqName: FqName): ReflectJavaAnnotation? {
    return firstOrNull { it.annotationClass.java.classId.asSingleFqName() == fqName }?.let(::ReflectJavaAnnotation)
}
