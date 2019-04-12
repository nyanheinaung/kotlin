/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.container

import com.intellij.util.containers.MultiMap
import java.lang.reflect.Type

internal class ComponentRegistry {
    fun buildRegistrationMap(descriptors: Collection<ComponentDescriptor>): MultiMap<Type, ComponentDescriptor> {
        val registrationMap = MultiMap<Type, ComponentDescriptor>()
        for (descriptor in descriptors) {
            for (registration in descriptor.getRegistrations()) {
                registrationMap.putValue(registration, descriptor)
            }
        }
        return registrationMap
    }

    private val registrationMap = hashMapOf<Type, Any>()

    fun addAll(descriptors: Collection<ComponentDescriptor>) {
        val newRegistrationMap = buildRegistrationMap(descriptors)
        for (entry in newRegistrationMap.entrySet()) {
            val oldEntries = registrationMap[entry.key]
            if (oldEntries != null || entry.value.size > 1) {
                val list = mutableListOf<ComponentDescriptor>()
                if (oldEntries is Collection<*>) {
                    @Suppress("UNCHECKED_CAST")
                    list.addAll(oldEntries as Collection<ComponentDescriptor>)
                }
                else if (oldEntries != null) {
                    list.add(oldEntries as ComponentDescriptor)
                }
                list.addAll(entry.value)
                registrationMap[entry.key] = list.singleOrNull() ?: list
            }
            else {
                registrationMap[entry.key] = entry.value.single()
            }
        }
    }

    fun tryGetEntry(request: Type): Collection<ComponentDescriptor> {
        val value = registrationMap[request]
        @Suppress("UNCHECKED_CAST")
        return when(value) {
            is Collection<*> -> value as Collection<ComponentDescriptor>
            null -> emptyList()
            else -> listOf(value as ComponentDescriptor)
        }
    }

    fun addAll(other: ComponentRegistry) {
        if (!registrationMap.isEmpty()) {
            throw IllegalStateException("Can only copy entries from another component registry into an empty component registry")
        }
        registrationMap += other.registrationMap
    }
}