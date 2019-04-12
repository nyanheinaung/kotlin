/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.container

import java.lang.reflect.*

open class InstanceComponentDescriptor(val instance: Any) : ComponentDescriptor {

    override fun getValue(): Any = instance
    override fun getRegistrations(): Iterable<Type> = instance::class.java.getInfo().registrations

    override fun getDependencies(context: ValueResolveContext): Collection<Class<*>> = emptyList()

    override fun toString(): String {
        return "Instance: ${instance::class.java.simpleName}"
    }
}

class DefaultInstanceComponentDescriptor(instance: Any): InstanceComponentDescriptor(instance) {
    override fun toString() = "Default instance: ${instance.javaClass.simpleName}"
}