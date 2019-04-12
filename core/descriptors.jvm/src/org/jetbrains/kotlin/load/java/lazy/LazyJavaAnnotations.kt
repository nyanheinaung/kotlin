/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.lazy

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.load.java.components.JavaAnnotationMapper
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaAnnotationOwner
import org.jetbrains.kotlin.name.FqName

class LazyJavaAnnotations(
    private val c: LazyJavaResolverContext,
    private val annotationOwner: JavaAnnotationOwner
) : Annotations {
    private val annotationDescriptors = c.components.storageManager.createMemoizedFunctionWithNullableValues { annotation: JavaAnnotation ->
        JavaAnnotationMapper.mapOrResolveJavaAnnotation(annotation, c)
    }

    override fun findAnnotation(fqName: FqName) =
        annotationOwner.findAnnotation(fqName)?.let(annotationDescriptors)
            ?: JavaAnnotationMapper.findMappedJavaAnnotation(fqName, annotationOwner, c)

    override fun iterator() =
        (annotationOwner.annotations.asSequence().map(annotationDescriptors) +
                JavaAnnotationMapper.findMappedJavaAnnotation(
                    KotlinBuiltIns.FQ_NAMES.deprecated,
                    annotationOwner,
                    c
                )).filterNotNull().iterator()

    override fun isEmpty() = annotationOwner.annotations.isEmpty() && !annotationOwner.isDeprecatedInJavaDoc
}

fun LazyJavaResolverContext.resolveAnnotations(annotationsOwner: JavaAnnotationOwner): Annotations =
    LazyJavaAnnotations(this, annotationsOwner)
