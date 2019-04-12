/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.model.functors

import org.jetbrains.kotlin.contracts.model.Computation
import org.jetbrains.kotlin.contracts.model.ESComponents
import org.jetbrains.kotlin.contracts.model.ESEffect
import org.jetbrains.kotlin.contracts.model.Functor
import org.jetbrains.kotlin.contracts.model.structure.ESConstants
import org.jetbrains.kotlin.contracts.model.visitors.Reducer

/**
 * Abstract implementation of Functor with some routine house-holding
 * automatically performed. *
 */
abstract class AbstractReducingFunctor(internal val constants: ESConstants) : Functor {
    private val reducer = Reducer(constants)

    override fun invokeWithArguments(arguments: List<Computation>): List<ESEffect> = reducer.reduceEffects(doInvocation(arguments))

    protected abstract fun doInvocation(arguments: List<Computation>): List<ESEffect>
}
