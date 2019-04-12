/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.common

import org.jetbrains.org.objectweb.asm.Type

interface ReferenceValueDescriptor {
    fun onUseAsTainted()
}

sealed class TrackedReferenceValue(type: Type) : StrictBasicValue(type) {
    abstract val descriptors: Set<ReferenceValueDescriptor>
}

class ProperTrackedReferenceValue(type: Type, val descriptor: ReferenceValueDescriptor) : TrackedReferenceValue(type) {
    override val descriptors: Set<ReferenceValueDescriptor>
        get() = setOf(descriptor)

    override fun equals(other: Any?): Boolean =
        other === this ||
                other is ProperTrackedReferenceValue && other.descriptor == this.descriptor

    override fun hashCode(): Int =
        descriptor.hashCode()

    override fun toString(): String =
        "[$descriptor]"
}


class MergedTrackedReferenceValue(type: Type, override val descriptors: Set<ReferenceValueDescriptor>) : TrackedReferenceValue(type) {
    override fun equals(other: Any?): Boolean =
        other === this ||
                other is MergedTrackedReferenceValue && other.descriptors == this.descriptors

    override fun hashCode(): Int =
        descriptors.hashCode()

    override fun toString(): String =
        descriptors.toString()
}


class TaintedTrackedReferenceValue(type: Type, override val descriptors: Set<ReferenceValueDescriptor>) : TrackedReferenceValue(type) {
    override fun equals(other: Any?): Boolean =
        other === this ||
                other is TaintedTrackedReferenceValue && other.descriptors == this.descriptors

    override fun hashCode(): Int =
        descriptors.hashCode()

    override fun toString(): String =
        "!$descriptors"
}
