/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationArgument
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

class ReflectJavaAnnotation(val annotation: Annotation) : ReflectJavaElement(), JavaAnnotation {
    override val arguments: Collection<JavaAnnotationArgument>
        get() = annotation.annotationClass.java.declaredMethods.map { method ->
            ReflectJavaAnnotationArgument.create(method.invoke(annotation), Name.identifier(method.name))
        }

    override val classId: ClassId
        get() = annotation.annotationClass.java.classId

    override fun resolve() = ReflectJavaClass(annotation.annotationClass.java)

    override fun equals(other: Any?) = other is ReflectJavaAnnotation && annotation == other.annotation

    override fun hashCode() = annotation.hashCode()

    override fun toString() = this::class.java.name + ": " + annotation
}
