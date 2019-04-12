/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model

/**
 * An abstraction of effect-generating nature of some computation.
 *
 * One can think of Functor as of adjoint to function declaration, responsible
 * for generating effects. It's [invokeWithArguments] method roughly corresponds
 * to call of corresponding function, but instead of taking values and returning
 * values, it takes effects and returns effects.
 */
interface Functor {
    fun invokeWithArguments(arguments: List<Computation>): List<ESEffect>
}