/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.inline

import org.jetbrains.kotlin.load.kotlin.header.KotlinClassHeader
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.Flags
import org.jetbrains.kotlin.metadata.deserialization.NameResolver
import org.jetbrains.kotlin.metadata.deserialization.TypeTable
import org.jetbrains.kotlin.metadata.deserialization.getExtensionOrNull
import org.jetbrains.kotlin.metadata.jvm.JvmProtoBuf
import org.jetbrains.kotlin.metadata.jvm.JvmProtoBuf.JvmMethodSignature
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil

fun inlineFunctionsJvmNames(header: KotlinClassHeader): Set<String> {
    val annotationData = header.data ?: return emptySet()
    val strings = header.strings ?: return emptySet()

    return when (header.kind) {
        KotlinClassHeader.Kind.CLASS -> {
            val (nameResolver, classProto) = JvmProtoBufUtil.readClassDataFrom(annotationData, strings)
            inlineFunctionsJvmNames(classProto.functionList, nameResolver, classProto.typeTable) +
            inlineAccessorsJvmNames(classProto.propertyList, nameResolver)
        }
        KotlinClassHeader.Kind.FILE_FACADE,
        KotlinClassHeader.Kind.MULTIFILE_CLASS_PART -> {
            val (nameResolver, packageProto) = JvmProtoBufUtil.readPackageDataFrom(annotationData, strings)
            inlineFunctionsJvmNames(packageProto.functionList, nameResolver, packageProto.typeTable) +
            inlineAccessorsJvmNames(packageProto.propertyList, nameResolver)
        }
        else -> emptySet()
    }
}

private fun inlineFunctionsJvmNames(functions: List<ProtoBuf.Function>, nameResolver: NameResolver, protoTypeTable: ProtoBuf.TypeTable): Set<String> {
    val typeTable = TypeTable(protoTypeTable)
    val inlineFunctions = functions.filter { Flags.IS_INLINE.get(it.flags) }
    val jvmNames = inlineFunctions.mapNotNull {
        JvmProtoBufUtil.getJvmMethodSignature(it, nameResolver, typeTable)?.asString()
    }
    return jvmNames.toSet()
}

private fun inlineAccessorsJvmNames(properties: List<ProtoBuf.Property>, nameResolver: NameResolver): Set<String> {
    val propertiesWithInlineAccessors = properties.filter { proto ->
        proto.hasGetterFlags() && Flags.IS_INLINE_ACCESSOR.get(proto.getterFlags) ||
        proto.hasSetterFlags() && Flags.IS_INLINE_ACCESSOR.get(proto.setterFlags)
    }
    val inlineAccessors = arrayListOf<JvmMethodSignature>()
    propertiesWithInlineAccessors.forEach { proto ->
        val signature = proto.getExtensionOrNull(JvmProtoBuf.propertySignature)
        if (signature != null) {
            if (proto.hasGetterFlags() && Flags.IS_INLINE_ACCESSOR.get(proto.getterFlags)) {
                inlineAccessors.add(signature.getter)
            }

            if (proto.hasSetterFlags() && Flags.IS_INLINE_ACCESSOR.get(proto.setterFlags)) {
                inlineAccessors.add(signature.setter)
            }
        }
    }

    return inlineAccessors.map {
        nameResolver.getString(it.name) + nameResolver.getString(it.desc)
    }.toSet()
}
