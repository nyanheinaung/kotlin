/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package kotlin.script.dependencies

import java.io.File

interface KotlinScriptExternalDependencies : Comparable<KotlinScriptExternalDependencies> {
    val javaHome: String? get() = null
    val classpath: Iterable<File> get() = emptyList()
    val imports: Iterable<String> get() = emptyList()
    val sources: Iterable<File> get() = emptyList()
    val scripts: Iterable<File> get() = emptyList()

    override fun compareTo(other: KotlinScriptExternalDependencies): Int =
            compareValues(javaHome, other.javaHome)
                    .chainCompare { compareIterables(classpath, other.classpath) }
                    .chainCompare { compareIterables(imports, other.imports) }
                    .chainCompare { compareIterables(sources, other.sources) }
                    .chainCompare { compareIterables(scripts, other.scripts) }
}

// copied form Comparisons.kt to resolve temporary build issues with dependency on stdlib
private fun <T : Comparable<*>> compareValues(a: T?, b: T?): Int {
    if (a === b) return 0
    if (a == null) return -1
    if (b == null) return 1

    @Suppress("UNCHECKED_CAST")
    return (a as Comparable<Any>).compareTo(b)
}

private fun <T : Comparable<T>> compareIterables(a: Iterable<T>, b: Iterable<T>): Int {
    val ia = a.iterator()
    val ib = b.iterator()
    while (true) {
        if (ia.hasNext() && !ib.hasNext()) return 1
        if (!ia.hasNext() && !ib.hasNext()) return 0
        if (!ia.hasNext()) return -1
        val compRes = compareValues(ia.next(), ib.next())
        if (compRes != 0) return compRes
    }
}

private inline fun Int.chainCompare(compFn: () -> Int): Int = if (this != 0) this else compFn()