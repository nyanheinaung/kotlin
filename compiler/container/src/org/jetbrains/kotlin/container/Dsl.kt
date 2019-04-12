/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.container

import kotlin.reflect.KProperty

fun composeContainer(id: String, parent: StorageComponentContainer? = null, init: StorageComponentContainer.() -> Unit): StorageComponentContainer {
    val c = StorageComponentContainer(id, parent)
    c.init()
    c.compose()
    return c
}

inline fun <reified T : Any> StorageComponentContainer.useImpl() {
    registerSingleton(T::class.java)
}

inline fun <reified T : Any> ComponentProvider.get(): T {
    return getService(T::class.java)
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> ComponentProvider.tryGetService(request: Class<T>): T? {
    return resolve(request)?.getValue() as T?
}

fun <T : Any> ComponentProvider.getService(request: Class<T>): T {
    return tryGetService(request) ?: throw UnresolvedServiceException(this, request)
}

fun StorageComponentContainer.useInstance(instance: Any) {
    registerInstance(instance)
}

inline operator fun <reified T : Any> ComponentProvider.getValue(thisRef: Any?, desc: KProperty<*>): T {
    return getService(T::class.java)
}

class UnresolvedServiceException(container: ComponentProvider, request: Class<*>) :
    IllegalArgumentException("Unresolved service: $request in $container")