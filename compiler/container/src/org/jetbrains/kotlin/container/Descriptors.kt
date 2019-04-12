/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.container

import java.lang.reflect.*

interface ValueDescriptor {
    fun getValue(): Any
}

internal interface ComponentDescriptor : ValueDescriptor {
    fun getRegistrations(): Iterable<Type>
    fun getDependencies(context: ValueResolveContext): Collection<Type>
    val shouldInjectProperties: Boolean
        get() = false
}

class IterableDescriptor(val descriptors: Iterable<ValueDescriptor>) : ValueDescriptor {
    override fun getValue(): Any {
        return descriptors.map { it.getValue() }
    }

    override fun toString(): String = "Iterable: $descriptors"
}