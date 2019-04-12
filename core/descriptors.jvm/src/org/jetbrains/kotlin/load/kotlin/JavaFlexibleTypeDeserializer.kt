/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import org.jetbrains.kotlin.load.java.lazy.types.RawTypeImpl
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.jvm.JvmProtoBuf
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.serialization.deserialization.FlexibleTypeDeserializer
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.KotlinTypeFactory
import org.jetbrains.kotlin.types.SimpleType

object JavaFlexibleTypeDeserializer : FlexibleTypeDeserializer {
    override fun create(proto: ProtoBuf.Type, flexibleId: String, lowerBound: SimpleType, upperBound: SimpleType): KotlinType {
        if (flexibleId != JvmProtoBufUtil.PLATFORM_TYPE_ID) {
            return ErrorUtils.createErrorType("Error java flexible type with id: $flexibleId. ($lowerBound..$upperBound)")
        }
        if (proto.hasExtension(JvmProtoBuf.isRaw)) {
            return RawTypeImpl(lowerBound, upperBound)
        }
        return KotlinTypeFactory.flexibleType(lowerBound, upperBound)
    }
}
