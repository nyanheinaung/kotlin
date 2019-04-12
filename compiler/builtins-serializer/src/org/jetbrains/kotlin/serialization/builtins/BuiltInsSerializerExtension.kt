/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.builtins

import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.builtins.BuiltInsBinaryVersion
import org.jetbrains.kotlin.metadata.deserialization.BinaryVersion
import org.jetbrains.kotlin.serialization.KotlinSerializerExtensionBase
import org.jetbrains.kotlin.serialization.deserialization.builtins.BuiltInSerializerProtocol
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.UnresolvedType

class BuiltInsSerializerExtension : KotlinSerializerExtensionBase(BuiltInSerializerProtocol) {
    private val shortNameToClassId = mapOf(
        "IntRange" to "kotlin/ranges/IntRange",
        "LongRange" to "kotlin/ranges/LongRange",
        "CharRange" to "kotlin/ranges/CharRange"
    )

    override val metadataVersion: BinaryVersion
        get() = BuiltInsBinaryVersion.INSTANCE

    override fun shouldUseTypeTable(): Boolean = true

    override fun serializeErrorType(type: KotlinType, builder: ProtoBuf.Type.Builder) {
        val unwrapped = type.unwrap()
        if (unwrapped !is UnresolvedType) {
            throw UnsupportedOperationException("Error types which are not UnresolvedType instances are not supported here: $unwrapped")
        }

        val className = shortNameToClassId[unwrapped.presentableName]
                ?: throw UnsupportedOperationException("Unsupported unresolved type: $unwrapped")

        builder.className = stringTable.getQualifiedClassNameIndex(className, false)
    }
}
