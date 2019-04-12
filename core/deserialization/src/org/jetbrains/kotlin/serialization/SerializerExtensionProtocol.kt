/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization

import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.protobuf.ExtensionRegistryLite
import org.jetbrains.kotlin.protobuf.GeneratedMessageLite.GeneratedExtension

open class SerializerExtensionProtocol(
    val extensionRegistry: ExtensionRegistryLite,
    val packageFqName: GeneratedExtension<ProtoBuf.Package, Int>,
    val constructorAnnotation: GeneratedExtension<ProtoBuf.Constructor, List<ProtoBuf.Annotation>>,
    val classAnnotation: GeneratedExtension<ProtoBuf.Class, List<ProtoBuf.Annotation>>,
    val functionAnnotation: GeneratedExtension<ProtoBuf.Function, List<ProtoBuf.Annotation>>,
    val propertyAnnotation: GeneratedExtension<ProtoBuf.Property, List<ProtoBuf.Annotation>>,
    val propertyGetterAnnotation: GeneratedExtension<ProtoBuf.Property, List<ProtoBuf.Annotation>>,
    val propertySetterAnnotation: GeneratedExtension<ProtoBuf.Property, List<ProtoBuf.Annotation>>,
    val enumEntryAnnotation: GeneratedExtension<ProtoBuf.EnumEntry, List<ProtoBuf.Annotation>>,
    val compileTimeValue: GeneratedExtension<ProtoBuf.Property, ProtoBuf.Annotation.Argument.Value>,
    val parameterAnnotation: GeneratedExtension<ProtoBuf.ValueParameter, List<ProtoBuf.Annotation>>,
    val typeAnnotation: GeneratedExtension<ProtoBuf.Type, List<ProtoBuf.Annotation>>,
    val typeParameterAnnotation: GeneratedExtension<ProtoBuf.TypeParameter, List<ProtoBuf.Annotation>>
)
