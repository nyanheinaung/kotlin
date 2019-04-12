/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm

import org.jetbrains.kotlin.load.java.JvmAnnotationNames
import org.jetbrains.kotlin.metadata.deserialization.TypeTable
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmMetadataVersion
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.serialization.deserialization.MemberDeserializer
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.internal.EmptyContainerForLocal
import kotlin.reflect.jvm.internal.KFunctionImpl
import kotlin.reflect.jvm.internal.deserializeToDescriptor

/**
 * This is an experimental API. Given a class for a compiled Kotlin lambda or a function expression,
 * returns a [KFunction] instance providing introspection capabilities for that lambda or function expression and its parameters.
 * Not all features are currently supported, in particular [KCallable.call] and [KCallable.callBy] will fail at the moment.
 */
fun <R> Function<R>.reflect(): KFunction<R>? {
    val annotation = javaClass.getAnnotation(Metadata::class.java) ?: return null
    val data = annotation.data1.takeUnless(Array<String>::isEmpty) ?: return null
    val (nameResolver, proto) = JvmProtoBufUtil.readFunctionDataFrom(data, annotation.data2)
    val metadataVersion = JvmMetadataVersion(
        annotation.metadataVersion,
        (annotation.extraInt and JvmAnnotationNames.METADATA_STRICT_VERSION_SEMANTICS_FLAG) != 0
    )

    val descriptor = deserializeToDescriptor(
        javaClass, proto, nameResolver, TypeTable(proto.typeTable), metadataVersion, MemberDeserializer::loadFunction
    ) ?: return null

    @Suppress("UNCHECKED_CAST")
    return KFunctionImpl(EmptyContainerForLocal, descriptor) as KFunction<R>
}
