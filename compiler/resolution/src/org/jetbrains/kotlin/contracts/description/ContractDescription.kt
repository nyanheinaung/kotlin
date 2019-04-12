/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.description

import org.jetbrains.kotlin.contracts.interpretation.ContractInterpretationDispatcher
import org.jetbrains.kotlin.contracts.model.ESComponents
import org.jetbrains.kotlin.contracts.model.Functor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.storage.StorageManager

/**
 * This is actual model of contracts, i.e. what is expected to be parsed from
 * general protobuf format.
 *
 * Its intention is to provide declarative representation which is more stable
 * than inner representation of effect system, while enforcing type-checking which
 * isn't possible in protobuf representation.
 *
 * Any changes to this model should be done with previous versions in mind to keep
 * backward compatibility. Ideally, this model should only be extended, but not
 * changed.
 */
open class ContractDescription(
    val effects: List<EffectDeclaration>,
    val ownerFunction: FunctionDescriptor,
    storageManager: StorageManager
) {
    private val computeFunctor = storageManager.createMemoizedFunctionWithNullableValues<ModuleDescriptor, Functor> { module ->
        val components = ESComponents(module.builtIns)
        ContractInterpretationDispatcher(components).convertContractDescriptorToFunctor(this)
    }

    fun getFunctor(usageModule: ModuleDescriptor): Functor? = computeFunctor(usageModule)
}

interface ContractDescriptionElement {
    fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R
}

interface EffectDeclaration : ContractDescriptionElement {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitEffectDeclaration(this, data)
}

interface BooleanExpression : ContractDescriptionElement {
    override fun <R, D> accept(contractDescriptionVisitor: ContractDescriptionVisitor<R, D>, data: D): R =
        contractDescriptionVisitor.visitBooleanExpression(this, data)
}
