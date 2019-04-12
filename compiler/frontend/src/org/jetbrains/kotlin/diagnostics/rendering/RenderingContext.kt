/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.diagnostics.rendering

import org.jetbrains.kotlin.diagnostics.*

// holds data about the parameters of the diagnostic we're about to render
sealed class RenderingContext {
    abstract operator fun <T> get(key: Key<T>): T

    abstract class Key<out T>(val name: String) {
        abstract fun compute(objectsToRender: Collection<Any?>): T
    }

    class Impl(private val objectsToRender: Collection<Any?>) : RenderingContext() {
        private val data = linkedMapOf<Key<*>, Any?>()

        @Suppress("UNCHECKED_CAST")
        override fun <T> get(key: Key<T>): T {
            return data[key] as? T ?: key.compute(objectsToRender).also { data[key] = it }
        }
    }

    object Empty : RenderingContext() {
        override fun <T> get(key: Key<T>): T {
            return key.compute(emptyList())
        }
    }

    companion object {
        @JvmStatic
        fun of(vararg objectsToRender: Any?): RenderingContext {
            return Impl(objectsToRender.toList())
        }

        @JvmStatic
        fun fromDiagnostic(d: Diagnostic): RenderingContext {
            val parameters = when (d) {
                is SimpleDiagnostic<*> -> listOf()
                is DiagnosticWithParameters1<*, *> -> listOf(d.a)
                is DiagnosticWithParameters2<*, *, *> -> listOf(d.a, d.b)
                is DiagnosticWithParameters3<*, *, *, *> -> listOf(d.a, d.b, d.c)
                is ParametrizedDiagnostic<*> -> error("Unexpected diagnostic: ${d::class.java}")
                else -> listOf()
            }
            return Impl(parameters)
        }
    }
}
