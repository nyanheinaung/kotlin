/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.NotFoundClasses
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.NameResolver
import org.jetbrains.kotlin.metadata.deserialization.getExtensionOrNull
import org.jetbrains.kotlin.protobuf.MessageLite
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.serialization.SerializerExtensionProtocol
import org.jetbrains.kotlin.types.KotlinType

class AnnotationAndConstantLoaderImpl(
    module: ModuleDescriptor,
    notFoundClasses: NotFoundClasses,
    private val protocol: SerializerExtensionProtocol
) : AnnotationAndConstantLoader<AnnotationDescriptor, ConstantValue<*>> {
    private val deserializer = AnnotationDeserializer(module, notFoundClasses)

    override fun loadClassAnnotations(container: ProtoContainer.Class): List<AnnotationDescriptor> {
        val annotations = container.classProto.getExtension(protocol.classAnnotation).orEmpty()
        return annotations.map { proto -> deserializer.deserializeAnnotation(proto, container.nameResolver) }
    }

    override fun loadCallableAnnotations(
        container: ProtoContainer,
        proto: MessageLite,
        kind: AnnotatedCallableKind
    ): List<AnnotationDescriptor> {
        val annotations = when (proto) {
            is ProtoBuf.Constructor -> proto.getExtension(protocol.constructorAnnotation)
            is ProtoBuf.Function -> proto.getExtension(protocol.functionAnnotation)
            is ProtoBuf.Property -> when (kind) {
                AnnotatedCallableKind.PROPERTY -> proto.getExtension(protocol.propertyAnnotation)
                AnnotatedCallableKind.PROPERTY_GETTER -> proto.getExtension(protocol.propertyGetterAnnotation)
                AnnotatedCallableKind.PROPERTY_SETTER -> proto.getExtension(protocol.propertySetterAnnotation)
                else -> error("Unsupported callable kind with property proto")
            }
            else -> error("Unknown message: $proto")
        }.orEmpty()
        return annotations.map { annotationProto ->
            deserializer.deserializeAnnotation(annotationProto, container.nameResolver)
        }
    }

    override fun loadPropertyBackingFieldAnnotations(container: ProtoContainer, proto: ProtoBuf.Property): List<AnnotationDescriptor> =
        emptyList()

    override fun loadPropertyDelegateFieldAnnotations(container: ProtoContainer, proto: ProtoBuf.Property): List<AnnotationDescriptor> =
        emptyList()

    override fun loadEnumEntryAnnotations(container: ProtoContainer, proto: ProtoBuf.EnumEntry): List<AnnotationDescriptor> {
        val annotations = proto.getExtension(protocol.enumEntryAnnotation).orEmpty()
        return annotations.map { annotationProto ->
            deserializer.deserializeAnnotation(annotationProto, container.nameResolver)
        }
    }

    override fun loadValueParameterAnnotations(
        container: ProtoContainer,
        callableProto: MessageLite,
        kind: AnnotatedCallableKind,
        parameterIndex: Int,
        proto: ProtoBuf.ValueParameter
    ): List<AnnotationDescriptor> {
        val annotations = proto.getExtension(protocol.parameterAnnotation).orEmpty()
        return annotations.map { annotationProto ->
            deserializer.deserializeAnnotation(annotationProto, container.nameResolver)
        }
    }

    override fun loadExtensionReceiverParameterAnnotations(
        container: ProtoContainer,
        proto: MessageLite,
        kind: AnnotatedCallableKind
    ): List<AnnotationDescriptor> = emptyList()

    override fun loadTypeAnnotations(proto: ProtoBuf.Type, nameResolver: NameResolver): List<AnnotationDescriptor> {
        return proto.getExtension(protocol.typeAnnotation).orEmpty().map { deserializer.deserializeAnnotation(it, nameResolver) }
    }

    override fun loadTypeParameterAnnotations(proto: ProtoBuf.TypeParameter, nameResolver: NameResolver): List<AnnotationDescriptor> {
        return proto.getExtension(protocol.typeParameterAnnotation).orEmpty().map { deserializer.deserializeAnnotation(it, nameResolver) }
    }

    override fun loadPropertyConstant(container: ProtoContainer, proto: ProtoBuf.Property, expectedType: KotlinType): ConstantValue<*>? {
        val value = proto.getExtensionOrNull(protocol.compileTimeValue) ?: return null
        return deserializer.resolveValue(expectedType, value, container.nameResolver)
    }
}
