/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal

import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.reflect.jvm.internal.components.RuntimeModuleData
import kotlin.reflect.jvm.internal.structure.safeClassLoader

// TODO: collect nulls periodically
private val moduleByClassLoader: ConcurrentMap<WeakClassLoaderBox, WeakReference<RuntimeModuleData>> = ConcurrentHashMap()

private class WeakClassLoaderBox(classLoader: ClassLoader) {
    val ref: WeakReference<ClassLoader> = WeakReference(classLoader)

    // Identity hash code is saved because otherwise once the weak reference is GC'd we cannot compute it anymore
    val identityHashCode: Int = System.identityHashCode(classLoader)

    // Temporary strong reference to the class loader to ensure it won't get GC'd while we're inserting this box into the map
    var temporaryStrongRef: ClassLoader? = classLoader

    override fun equals(other: Any?) =
        other is WeakClassLoaderBox && ref.get() === other.ref.get()

    override fun hashCode() =
        identityHashCode

    override fun toString() =
        ref.get()?.toString() ?: "<null>"
}

internal fun Class<*>.getOrCreateModule(): RuntimeModuleData {
    val classLoader = this.safeClassLoader

    val key = WeakClassLoaderBox(classLoader)

    val cached = moduleByClassLoader[key]
    if (cached != null) {
        cached.get()?.let { return it }
        moduleByClassLoader.remove(key, cached)
    }

    val module = RuntimeModuleData.create(classLoader)
    try {
        while (true) {
            val ref = moduleByClassLoader.putIfAbsent(key, WeakReference(module)) ?: return module

            val result = ref.get()
            if (result != null) return result
            moduleByClassLoader.remove(key, ref)
        }
    } finally {
        key.temporaryStrongRef = null
    }
}

internal fun clearModuleByClassLoaderCache() {
    moduleByClassLoader.clear()
}
