/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.build

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

inline fun <reified T : Any> serializeToPlainText(instance: T): String = serializeToPlainText(instance, T::class)

fun <T : Any> serializeToPlainText(instance: T, klass: KClass<T>): String {
    val lines = ArrayList<String>()
    for (property in klass.memberProperties) {
        val value = property.get(instance)
        if (value != null) {
            lines.add("${property.name}=$value")
        }
    }
    return lines.joinToString("\n")
}

inline fun <reified T : Any> deserializeFromPlainText(str: String): T? = deserializeFromPlainText(str, T::class)

fun <T : Any> deserializeFromPlainText(str: String, klass: KClass<T>): T? {
    val args = ArrayList<Any?>()
    val properties = str
            .split("\n")
            .filter(String::isNotBlank)
            .associate { it.substringBefore("=") to it.substringAfter("=") }

    val primaryConstructor = klass.primaryConstructor
                             ?: throw IllegalStateException("${klass.java} does not have primary constructor")
    for (param in primaryConstructor.parameters.sortedBy { it.index }) {
        val argumentString = properties[param.name]

        if (argumentString == null) {
            if (param.type.isMarkedNullable) {
                args.add(null)
                continue
            }
            else {
                return null
            }
        }

        val argument: Any? = when (param.type.classifier) {
            Int::class -> argumentString.toInt()
            Boolean::class -> argumentString.toBoolean()
            String::class -> argumentString
            else -> throw IllegalStateException("Unexpected property type: ${param.type}")
        }

        args.add(argument)
    }

    return primaryConstructor.call(*args.toTypedArray())
}
