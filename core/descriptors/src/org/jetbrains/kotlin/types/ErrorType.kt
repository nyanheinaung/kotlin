/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.types

import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.resolve.scopes.MemberScope

open class ErrorType @JvmOverloads internal constructor(
        override val constructor: TypeConstructor,
        override val memberScope: MemberScope,
        override val arguments: List<TypeProjection> = emptyList(),
        override val isMarkedNullable: Boolean = false
) : SimpleType() {
    override val annotations: Annotations
        get() = Annotations.EMPTY

    override fun toString(): String =
            constructor.toString() + if (arguments.isEmpty()) "" else arguments.joinToString(", ", "<", ">", -1, "...", null)

    override fun replaceAnnotations(newAnnotations: Annotations): SimpleType = this

    override fun makeNullableAsSpecified(newNullability: Boolean): SimpleType =
            ErrorType(constructor, memberScope, arguments, newNullability)
}

class UnresolvedType(
        val presentableName: String,
        constructor: TypeConstructor,
        memberScope: MemberScope,
        arguments: List<TypeProjection>,
        isMarkedNullable: Boolean
) : ErrorType(constructor, memberScope, arguments, isMarkedNullable) {
    override fun makeNullableAsSpecified(newNullability: Boolean): SimpleType =
            UnresolvedType(presentableName, constructor, memberScope, arguments, newNullability)
}
