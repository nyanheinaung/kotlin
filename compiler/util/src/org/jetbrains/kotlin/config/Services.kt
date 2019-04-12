/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config

import java.util.*

class Services private constructor(private val map: Map<Class<*>, Any>) {
    companion object {
        @JvmField
        val EMPTY: Services = Builder().build()
    }

    operator fun <T> get(interfaceClass: Class<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return map[interfaceClass] as T?
    }

    class Builder {
        private val map = HashMap<Class<*>, Any>()

        fun <T : Any> register(interfaceClass: Class<T>, implementation: T): Builder {
            map.put(interfaceClass, implementation)
            return this
        }

        fun build(): Services {
            return Services(map)
        }
    }
}
