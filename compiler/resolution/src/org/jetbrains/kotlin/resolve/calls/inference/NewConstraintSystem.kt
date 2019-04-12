/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.resolve.calls.components.KotlinCallCompleter
import org.jetbrains.kotlin.resolve.calls.components.PostponedArgumentsAnalyzer
import org.jetbrains.kotlin.resolve.calls.inference.components.KotlinConstraintSystemCompleter
import org.jetbrains.kotlin.resolve.calls.inference.model.ConstraintStorage
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallDiagnostic

interface NewConstraintSystem {
//    val builtIns: KotlinBuiltIns
    val hasContradiction: Boolean
    val diagnostics: List<KotlinCallDiagnostic>

    fun getBuilder(): ConstraintSystemBuilder

    // after this method we shouldn't mutate system via ConstraintSystemBuilder
    fun asReadOnlyStorage(): ConstraintStorage

    fun asConstraintSystemCompleterContext(): KotlinConstraintSystemCompleter.Context
    fun asPostponedArgumentsAnalyzerContext(): PostponedArgumentsAnalyzer.Context
}