/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.interpretation

import org.jetbrains.kotlin.contracts.description.BooleanExpression
import org.jetbrains.kotlin.contracts.description.ConditionalEffectDeclaration
import org.jetbrains.kotlin.contracts.description.ContractDescription
import org.jetbrains.kotlin.contracts.description.EffectDeclaration
import org.jetbrains.kotlin.contracts.description.expressions.ConstantReference
import org.jetbrains.kotlin.contracts.description.expressions.VariableReference
import org.jetbrains.kotlin.contracts.model.ESComponents
import org.jetbrains.kotlin.contracts.model.ESEffect
import org.jetbrains.kotlin.contracts.model.ESExpression
import org.jetbrains.kotlin.contracts.model.Functor
import org.jetbrains.kotlin.contracts.model.functors.SubstitutingFunctor
import org.jetbrains.kotlin.contracts.model.structure.ESConstant
import org.jetbrains.kotlin.contracts.model.structure.ESVariable

/**
 * This class manages conversion of [ContractDescription] to [Functor]
 */
class ContractInterpretationDispatcher(val components: ESComponents) {
    private val constantsInterpreter = ConstantValuesInterpreter()
    private val conditionInterpreter = ConditionInterpreter(this)
    private val conditionalEffectInterpreter = ConditionalEffectInterpreter(this)
    private val effectsInterpreters: List<EffectDeclarationInterpreter> = listOf(
        ReturnsEffectInterpreter(this),
        CallsEffectInterpreter(this)
    )

    fun convertContractDescriptorToFunctor(contractDescription: ContractDescription): Functor? {
        val resultingClauses = contractDescription.effects.map { effect ->
            if (effect is ConditionalEffectDeclaration) {
                conditionalEffectInterpreter.interpret(effect) ?: return null
            } else {
                effectsInterpreters.mapNotNull { it.tryInterpret(effect) }.singleOrNull() ?: return null
            }
        }

        return SubstitutingFunctor(components, resultingClauses, contractDescription.ownerFunction)
    }

    internal fun interpretEffect(effectDeclaration: EffectDeclaration): ESEffect? {
        val convertedFunctors = effectsInterpreters.mapNotNull { it.tryInterpret(effectDeclaration) }
        return convertedFunctors.singleOrNull()
    }

    internal fun interpretConstant(constantReference: ConstantReference): ESConstant? =
        constantsInterpreter.interpretConstant(constantReference, components.constants)

    internal fun interpretCondition(booleanExpression: BooleanExpression): ESExpression? =
        booleanExpression.accept(conditionInterpreter, Unit)

    internal fun interpretVariable(variableReference: VariableReference): ESVariable? = ESVariable(variableReference.descriptor)
}
