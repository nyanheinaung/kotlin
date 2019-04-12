/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.builtins.KotlinBuiltIns.FQ_NAMES
import org.jetbrains.kotlin.name.FqName

object FakePureImplementationsProvider {
    fun getPurelyImplementedInterface(classFqName: FqName): FqName? = pureImplementations[classFqName]

    private val pureImplementations = hashMapOf<FqName, FqName>()
    private infix fun FqName.implementedWith(implementations: List<FqName>) {
        implementations.associateWithTo(pureImplementations) { this }
    }

    init {
        FQ_NAMES.mutableList implementedWith fqNameListOf("java.util.ArrayList", "java.util.LinkedList")
        FQ_NAMES.mutableSet implementedWith fqNameListOf("java.util.HashSet", "java.util.TreeSet", "java.util.LinkedHashSet")
        FQ_NAMES.mutableMap implementedWith fqNameListOf(
            "java.util.HashMap", "java.util.TreeMap", "java.util.LinkedHashMap",
            "java.util.concurrent.ConcurrentHashMap", "java.util.concurrent.ConcurrentSkipListMap"
        )
        FqName("java.util.function.Function") implementedWith fqNameListOf("java.util.function.UnaryOperator")
        FqName("java.util.function.BiFunction") implementedWith fqNameListOf("java.util.function.BinaryOperator")
    }

    private fun fqNameListOf(vararg names: String): List<FqName> = names.map(::FqName)
}
