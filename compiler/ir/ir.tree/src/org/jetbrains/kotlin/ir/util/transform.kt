/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.util

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer

inline fun <reified T : IrElement> MutableList<T>.transform(transformation: (T) -> IrElement) {
    forEachIndexed { i, item ->
        set(i, transformation(item) as T)
    }
}

/**
 * Transforms a mutable list in place.
 * Each element `it` is replaced with a result of `transformation(it)`,
 * `null` means "keep existing element" (to avoid creating excessive singleton lists).
 */
inline fun <T> MutableList<T>.transformFlat(transformation: (T) -> List<T>?) {
    var i = 0
    while (i < size) {
        val item = get(i)

        val transformed = transformation(item)

        if (transformed == null) {
            i++
            continue
        }

        addAll(i, transformed)
        i += transformed.size
        removeAt(i)
    }
}

/**
 * Transforms declarations in declaration container.
 * Behaves similar to like MutableList<T>.transformFlat but also updates
 * parent property for transformed declarations.
 */
fun IrDeclarationContainer.transformDeclarationsFlat(transformation: (IrDeclaration) -> List<IrDeclaration>?) {
    declarations.transformFlat { declaration ->
        val transformed = transformation(declaration)
        transformed?.forEach { it.parent = this }
        transformed
    }
}
