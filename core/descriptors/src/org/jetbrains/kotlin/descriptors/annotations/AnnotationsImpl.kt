/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations

/**
 * Use [Annotations.create] to create an instance of this class if necessary.
 */
internal class AnnotationsImpl(private val annotations: List<AnnotationDescriptor>) : Annotations {
    override fun isEmpty(): Boolean = annotations.isEmpty()

    override fun iterator(): Iterator<AnnotationDescriptor> = annotations.iterator()

    override fun toString(): String = annotations.toString()
}
