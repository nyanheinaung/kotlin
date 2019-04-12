/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.model

import org.jetbrains.kotlin.resolve.calls.tower.ResolutionCandidateApplicability
import org.jetbrains.kotlin.resolve.calls.tower.ResolutionCandidateApplicability.INAPPLICABLE
import org.jetbrains.kotlin.types.KotlinType

abstract class KotlinCallDiagnostic(val candidateApplicability: ResolutionCandidateApplicability) {
    abstract fun report(reporter: DiagnosticReporter)
}

interface DiagnosticReporter {
    fun onExplicitReceiver(diagnostic: KotlinCallDiagnostic)

    fun onCall(diagnostic: KotlinCallDiagnostic)

    fun onTypeArguments(diagnostic: KotlinCallDiagnostic)

    fun onCallName(diagnostic: KotlinCallDiagnostic)

    fun onTypeArgument(typeArgument: TypeArgument, diagnostic: KotlinCallDiagnostic)

    fun onCallReceiver(callReceiver: SimpleKotlinCallArgument, diagnostic: KotlinCallDiagnostic)

    fun onCallArgument(callArgument: KotlinCallArgument, diagnostic: KotlinCallDiagnostic)
    fun onCallArgumentName(callArgument: KotlinCallArgument, diagnostic: KotlinCallDiagnostic)
    fun onCallArgumentSpread(callArgument: KotlinCallArgument, diagnostic: KotlinCallDiagnostic)

    fun constraintError(diagnostic: KotlinCallDiagnostic)
}
