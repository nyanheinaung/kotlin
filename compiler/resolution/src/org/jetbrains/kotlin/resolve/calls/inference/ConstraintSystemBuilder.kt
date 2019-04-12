/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.resolve.calls.components.PostponedArgumentsAnalyzer
import org.jetbrains.kotlin.resolve.calls.inference.components.NewTypeSubstitutor
import org.jetbrains.kotlin.resolve.calls.inference.model.ConstraintPosition
import org.jetbrains.kotlin.resolve.calls.inference.model.ConstraintStorage
import org.jetbrains.kotlin.resolve.calls.inference.model.NewTypeVariable
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.types.model.KotlinTypeMarker
import org.jetbrains.kotlin.types.model.TypeConstructorMarker
import org.jetbrains.kotlin.types.model.TypeVariableMarker
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

interface ConstraintSystemOperation {
    val hasContradiction: Boolean
    fun registerVariable(variable: TypeVariableMarker)
    fun markPostponedVariable(variable: TypeVariableMarker)
    fun unmarkPostponedVariable(variable: TypeVariableMarker)

    fun addSubtypeConstraint(lowerType: KotlinTypeMarker, upperType: KotlinTypeMarker, position: ConstraintPosition)
    fun addEqualityConstraint(a: KotlinTypeMarker, b: KotlinTypeMarker, position: ConstraintPosition)

    fun isProperType(type: KotlinTypeMarker): Boolean
    fun isTypeVariable(type: KotlinTypeMarker): Boolean
    fun isPostponedTypeVariable(typeVariable: TypeVariableMarker): Boolean

    fun getProperSuperTypeConstructors(type: KotlinTypeMarker): List<TypeConstructorMarker>
}

interface ConstraintSystemBuilder : ConstraintSystemOperation {
    //val builtIns: KotlinBuiltIns
    // if runOperations return true, then this operation will be applied, and function return true
    fun runTransaction(runOperations: ConstraintSystemOperation.() -> Boolean): Boolean

    fun buildCurrentSubstitutor(): NewTypeSubstitutor

    fun currentStorage(): ConstraintStorage
}

fun ConstraintSystemBuilder.addSubtypeConstraintIfCompatible(
    lowerType: KotlinTypeMarker,
    upperType: KotlinTypeMarker,
    position: ConstraintPosition
) =
    runTransaction {
        if (!hasContradiction) addSubtypeConstraint(lowerType, upperType, position)
        !hasContradiction
    }


fun PostponedArgumentsAnalyzer.Context.addSubsystemFromArgument(argument: KotlinCallArgument?) {
    when (argument) {
        is SubKotlinCallArgument -> addOtherSystem(argument.callResult.constraintSystem)
        is CallableReferenceKotlinCallArgument -> {
            addSubsystemFromArgument(argument.lhsResult.safeAs<LHSResult.Expression>()?.lshCallArgument)
        }
    }
}