/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations

import org.jetbrains.kotlin.name.FqName

interface Annotated {
    val annotations: Annotations
}

interface Annotations : Iterable<AnnotationDescriptor> {
    fun isEmpty(): Boolean

    fun findAnnotation(fqName: FqName): AnnotationDescriptor? = firstOrNull { it.fqName == fqName }

    fun hasAnnotation(fqName: FqName): Boolean = findAnnotation(fqName) != null

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("This method should only be used in frontend where we split annotations according to their use-site targets.")
    fun getUseSiteTargetedAnnotations(): List<AnnotationWithTarget> = emptyList()

    companion object {
        val EMPTY: Annotations = object : Annotations {
            override fun isEmpty() = true

            override fun findAnnotation(fqName: FqName) = null

            override fun iterator() = emptyList<AnnotationDescriptor>().iterator()

            override fun toString() = "EMPTY"
        }

        fun create(annotations: List<AnnotationDescriptor>): Annotations =
            if (annotations.isEmpty()) EMPTY else AnnotationsImpl(annotations)
    }
}

class FilteredAnnotations(
        private val delegate: Annotations,
        private val fqNameFilter: (FqName) -> Boolean
) : Annotations {

    override fun hasAnnotation(fqName: FqName) =
            if (fqNameFilter(fqName)) delegate.hasAnnotation(fqName)
            else false

    override fun findAnnotation(fqName: FqName) =
            if (fqNameFilter(fqName)) delegate.findAnnotation(fqName)
            else null

    override fun iterator() = delegate.filter(this::shouldBeReturned).iterator()

    override fun isEmpty() = delegate.any(this::shouldBeReturned)

    private fun shouldBeReturned(annotation: AnnotationDescriptor): Boolean =
            annotation.fqName.let { fqName ->
                fqName != null && fqNameFilter(fqName)
            }
}

class CompositeAnnotations(
        private val delegates: List<Annotations>
) : Annotations {
    constructor(vararg delegates: Annotations): this(delegates.toList())

    override fun isEmpty() = delegates.all { it.isEmpty() }

    override fun hasAnnotation(fqName: FqName) = delegates.asSequence().any { it.hasAnnotation(fqName) }

    override fun findAnnotation(fqName: FqName) = delegates.asSequence().mapNotNull { it.findAnnotation(fqName) }.firstOrNull()

    @Suppress("DEPRECATION", "OverridingDeprecatedMember")
    override fun getUseSiteTargetedAnnotations() = delegates.flatMap { it.getUseSiteTargetedAnnotations() }

    override fun iterator() = delegates.asSequence().flatMap { it.asSequence() }.iterator()
}

fun composeAnnotations(first: Annotations, second: Annotations) =
        when {
            first.isEmpty() -> second
            second.isEmpty() -> first
            else -> CompositeAnnotations(first, second)
        }
