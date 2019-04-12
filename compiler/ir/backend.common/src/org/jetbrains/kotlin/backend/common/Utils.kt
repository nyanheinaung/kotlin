/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFile

fun CommonBackendContext.reportWarning(message: String, irFile: IrFile?, irElement: IrElement) {
    report(irElement, irFile, message, false)
}

fun <E> MutableList<E>.push(element: E) = this.add(element)

fun <E> MutableList<E>.pop() = this.removeAt(size - 1)

fun <E> MutableList<E>.peek(): E? = if (size == 0) null else this[size - 1]

fun <T> Collection<T>.atMostOne(): T? {
    return when (this.size) {
        0 -> null
        1 -> this.iterator().next()
        else -> throw IllegalArgumentException("Collection has more than one element.")
    }
}

inline fun <T> Iterable<T>.atMostOne(predicate: (T) -> Boolean): T? = this.filter(predicate).atMostOne()

fun <T: Any> T.onlyIf(condition: T.()->Boolean, then: (T)->Unit): T {
    if (this.condition()) then(this)
    return this
}
