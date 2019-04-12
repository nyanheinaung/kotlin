/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.metadata.deserialization

import org.jetbrains.kotlin.metadata.ProtoBuf

// TODO: return null and report a diagnostic instead of throwing exceptions

fun ProtoBuf.Class.supertypes(typeTable: TypeTable): List<ProtoBuf.Type> =
    supertypeList.takeIf(Collection<*>::isNotEmpty) ?: supertypeIdList.map { typeTable[it] }

fun ProtoBuf.Type.Argument.type(typeTable: TypeTable): ProtoBuf.Type? = when {
    hasType() -> type
    hasTypeId() -> typeTable[typeId]
    else -> null
}

fun ProtoBuf.Type.flexibleUpperBound(typeTable: TypeTable): ProtoBuf.Type? = when {
    hasFlexibleUpperBound() -> flexibleUpperBound
    hasFlexibleUpperBoundId() -> typeTable[flexibleUpperBoundId]
    else -> null
}

fun ProtoBuf.TypeParameter.upperBounds(typeTable: TypeTable): List<ProtoBuf.Type> =
    upperBoundList.takeIf(Collection<*>::isNotEmpty) ?: upperBoundIdList.map { typeTable[it] }

fun ProtoBuf.Function.returnType(typeTable: TypeTable): ProtoBuf.Type = when {
    hasReturnType() -> returnType
    hasReturnTypeId() -> typeTable[returnTypeId]
    else -> error("No returnType in ProtoBuf.Function")
}

fun ProtoBuf.Function.hasReceiver(): Boolean = hasReceiverType() || hasReceiverTypeId()

fun ProtoBuf.Function.receiverType(typeTable: TypeTable): ProtoBuf.Type? = when {
    hasReceiverType() -> receiverType
    hasReceiverTypeId() -> typeTable[receiverTypeId]
    else -> null
}

fun ProtoBuf.Property.returnType(typeTable: TypeTable): ProtoBuf.Type = when {
    hasReturnType() -> returnType
    hasReturnTypeId() -> typeTable[returnTypeId]
    else -> error("No returnType in ProtoBuf.Property")
}

fun ProtoBuf.Property.hasReceiver(): Boolean = hasReceiverType() || hasReceiverTypeId()

fun ProtoBuf.Property.receiverType(typeTable: TypeTable): ProtoBuf.Type? = when {
    hasReceiverType() -> receiverType
    hasReceiverTypeId() -> typeTable[receiverTypeId]
    else -> null
}

fun ProtoBuf.ValueParameter.type(typeTable: TypeTable): ProtoBuf.Type = when {
    hasType() -> type
    hasTypeId() -> typeTable[typeId]
    else -> error("No type in ProtoBuf.ValueParameter")
}

fun ProtoBuf.ValueParameter.varargElementType(typeTable: TypeTable): ProtoBuf.Type? = when {
    hasVarargElementType() -> varargElementType
    hasVarargElementTypeId() -> typeTable[varargElementTypeId]
    else -> null
}

fun ProtoBuf.Type.outerType(typeTable: TypeTable): ProtoBuf.Type? = when {
    hasOuterType() -> outerType
    hasOuterTypeId() -> typeTable[outerTypeId]
    else -> null
}

fun ProtoBuf.Type.abbreviatedType(typeTable: TypeTable): ProtoBuf.Type? = when {
    hasAbbreviatedType() -> abbreviatedType
    hasAbbreviatedTypeId() -> typeTable[abbreviatedTypeId]
    else -> null
}

fun ProtoBuf.TypeAlias.underlyingType(typeTable: TypeTable): ProtoBuf.Type = when {
    hasUnderlyingType() -> underlyingType
    hasUnderlyingTypeId() -> typeTable[underlyingTypeId]
    else -> error("No underlyingType in ProtoBuf.TypeAlias")
}

fun ProtoBuf.TypeAlias.expandedType(typeTable: TypeTable): ProtoBuf.Type = when {
    hasExpandedType() -> expandedType
    hasExpandedTypeId() -> typeTable[expandedTypeId]
    else -> error("No expandedType in ProtoBuf.TypeAlias")
}

fun ProtoBuf.Expression.isInstanceType(typeTable: TypeTable): ProtoBuf.Type? = when {
    hasIsInstanceType() -> isInstanceType
    hasIsInstanceTypeId() -> typeTable[isInstanceTypeId]
    else -> null
}
