/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization.descriptors

import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker
import org.jetbrains.kotlin.descriptors.impl.AbstractLazyTypeParameterDescriptor
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.upperBounds
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.serialization.deserialization.DeserializationContext
import org.jetbrains.kotlin.serialization.deserialization.ProtoEnumFlags
import org.jetbrains.kotlin.serialization.deserialization.getName
import org.jetbrains.kotlin.types.KotlinType

class DeserializedTypeParameterDescriptor(
    private val c: DeserializationContext,
    val proto: ProtoBuf.TypeParameter,
    index: Int
) : AbstractLazyTypeParameterDescriptor(
    c.storageManager, c.containingDeclaration, c.nameResolver.getName(proto.name),
    ProtoEnumFlags.variance(proto.variance), proto.reified, index, SourceElement.NO_SOURCE, SupertypeLoopChecker.EMPTY
) {
    override val annotations = DeserializedAnnotations(c.storageManager) {
        c.components.annotationAndConstantLoader.loadTypeParameterAnnotations(proto, c.nameResolver).toList()
    }

    override fun resolveUpperBounds(): List<KotlinType> {
        val upperBounds = proto.upperBounds(c.typeTable)
        if (upperBounds.isEmpty()) {
            return listOf(this.builtIns.defaultBound)
        }
        return upperBounds.map(c.typeDeserializer::type)
    }

    override fun reportSupertypeLoopError(type: KotlinType) = throw IllegalStateException(
        "There should be no cycles for deserialized type parameters, but found for: $this"
    )
}
