/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization.descriptors

import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.storage.getValue

open class DeserializedAnnotations(
    storageManager: StorageManager,
    compute: () -> List<AnnotationDescriptor>
) : Annotations {
    private val annotations by storageManager.createLazyValue(compute)

    override fun isEmpty(): Boolean = annotations.isEmpty()

    override fun iterator(): Iterator<AnnotationDescriptor> = annotations.iterator()
}

class NonEmptyDeserializedAnnotations(
    storageManager: StorageManager,
    compute: () -> List<AnnotationDescriptor>
) : DeserializedAnnotations(storageManager, compute) {
    override fun isEmpty(): Boolean = false
}
