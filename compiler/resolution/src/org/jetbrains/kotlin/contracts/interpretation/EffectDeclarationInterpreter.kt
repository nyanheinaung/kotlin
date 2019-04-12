/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.interpretation

import org.jetbrains.kotlin.contracts.description.EffectDeclaration
import org.jetbrains.kotlin.contracts.model.ESEffect

/**
 * Interpreter of some effects. For most cases, each particular
 * EffectDeclaration, EffectDeclarationInterpreter and ESEffect
 * should be in 1-1-1 correspondence to each other and be agnostic
 * about other implementations.
 */
internal interface EffectDeclarationInterpreter {
    fun tryInterpret(effectDeclaration: EffectDeclaration): ESEffect?
}