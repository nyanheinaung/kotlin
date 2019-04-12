/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.interpretation

import org.jetbrains.kotlin.contracts.description.CallsEffectDeclaration
import org.jetbrains.kotlin.contracts.description.ConditionalEffectDeclaration
import org.jetbrains.kotlin.contracts.description.EffectDeclaration
import org.jetbrains.kotlin.contracts.description.ReturnsEffectDeclaration
import org.jetbrains.kotlin.contracts.model.ConditionalEffect
import org.jetbrains.kotlin.contracts.model.ESEffect
import org.jetbrains.kotlin.contracts.model.SimpleEffect
import org.jetbrains.kotlin.contracts.model.structure.ESCalls
import org.jetbrains.kotlin.contracts.model.structure.ESReturns

internal class CallsEffectInterpreter(private val dispatcher: ContractInterpretationDispatcher) : EffectDeclarationInterpreter {
    override fun tryInterpret(effectDeclaration: EffectDeclaration): ESEffect? {
        if (effectDeclaration !is CallsEffectDeclaration) return null

        val variable = dispatcher.interpretVariable(effectDeclaration.variableReference) ?: return null
        val kind = effectDeclaration.kind
        return ESCalls(variable, kind)
    }
}

internal class ConditionalEffectInterpreter(private val dispatcher: ContractInterpretationDispatcher) {
    fun interpret(conditionalEffectDeclaration: ConditionalEffectDeclaration): ConditionalEffect? {
        val effect = dispatcher.interpretEffect(conditionalEffectDeclaration.effect) as? SimpleEffect ?: return null
        val condition = dispatcher.interpretCondition(conditionalEffectDeclaration.condition) ?: return null

        return ConditionalEffect(condition, effect)
    }
}

internal class ReturnsEffectInterpreter(private val dispatcher: ContractInterpretationDispatcher) : EffectDeclarationInterpreter {
    override fun tryInterpret(effectDeclaration: EffectDeclaration): ESEffect? {
        if (effectDeclaration !is ReturnsEffectDeclaration) return null
        val returnedValue = dispatcher.interpretConstant(effectDeclaration.value) ?: return null
        return ESReturns(returnedValue)
    }
}
