/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.classes

import com.intellij.psi.impl.light.LightElement
import com.intellij.util.IncorrectOperationException

// NOTE: avoid using blocking lazy in light classes, it leads to deadlocks
fun <T> lazyPub(initializer: () -> T) = lazy(LazyThreadSafetyMode.PUBLICATION, initializer)

fun LightElement.cannotModify(): Nothing {
    throw IncorrectOperationException("Modification not implemented.")
}