/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.inference.model

import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.resolve.calls.tower.ResolutionCandidateApplicability
import org.jetbrains.kotlin.resolve.calls.tower.ResolutionCandidateApplicability.INAPPLICABLE
import org.jetbrains.kotlin.resolve.calls.tower.ResolutionCandidateApplicability.INAPPLICABLE_WRONG_RECEIVER
import org.jetbrains.kotlin.resolve.scopes.receivers.QualifierReceiver
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.model.KotlinTypeMarker
import org.jetbrains.kotlin.types.model.TypeVariableMarker


sealed class ConstraintPosition

class ExplicitTypeParameterConstraintPosition(val typeArgument: SimpleTypeArgument) : ConstraintPosition() {
    override fun toString() = "TypeParameter $typeArgument"
}

class ExpectedTypeConstraintPosition(val topLevelCall: KotlinCall) : ConstraintPosition() {
    override fun toString() = "ExpectedType for call $topLevelCall"
}

class DeclaredUpperBoundConstraintPosition(val typeParameterDescriptor: TypeParameterDescriptor) : ConstraintPosition() {
    override fun toString() = "DeclaredUpperBound ${typeParameterDescriptor.name} from ${typeParameterDescriptor.containingDeclaration}"
}

class ArgumentConstraintPosition(val argument: KotlinCallArgument) : ConstraintPosition() {
    override fun toString() = "Argument $argument"
}

class ReceiverConstraintPosition(val argument: KotlinCallArgument) : ConstraintPosition() {
    override fun toString() = "Receiver $argument"
}

class FixVariableConstraintPosition(val variable: TypeVariableMarker) : ConstraintPosition() {
    override fun toString() = "Fix variable $variable"
}

class KnownTypeParameterConstraintPosition(val typeArgument: KotlinType) : ConstraintPosition() {
    override fun toString() = "TypeArgument $typeArgument"
}

class LHSArgumentConstraintPosition(val receiver: QualifierReceiver) : ConstraintPosition() {
    override fun toString(): String {
        return "LHS receiver $receiver"
    }
}

class LambdaArgumentConstraintPosition(val lambda: ResolvedLambdaAtom) : ConstraintPosition() {
    override fun toString(): String {
        return "LambdaArgument $lambda"
    }
}

class DelegatedPropertyConstraintPosition(val topLevelCall: KotlinCall) : ConstraintPosition() {
    override fun toString() = "Constraint from call $topLevelCall for delegated property"
}

class IncorporationConstraintPosition(val from: ConstraintPosition, val initialConstraint: InitialConstraint) : ConstraintPosition() {
    override fun toString() = "Incorporate $initialConstraint from position $from"
}

class CoroutinePosition() : ConstraintPosition() {
    override fun toString(): String = "for coroutine call"
}

@Deprecated("Should be used only in SimpleConstraintSystemImpl")
object SimpleConstraintSystemConstraintPosition : ConstraintPosition()

abstract class ConstraintSystemCallDiagnostic(applicability: ResolutionCandidateApplicability) : KotlinCallDiagnostic(applicability) {
    override fun report(reporter: DiagnosticReporter) = reporter.constraintError(this)
}

class NewConstraintError(
    val lowerType: KotlinTypeMarker,
    val upperType: KotlinTypeMarker,
    val position: IncorporationConstraintPosition
) : ConstraintSystemCallDiagnostic(if (position.from is ReceiverConstraintPosition) INAPPLICABLE_WRONG_RECEIVER else INAPPLICABLE)

class CapturedTypeFromSubtyping(
    val typeVariable: TypeVariableMarker,
    val constraintType: KotlinTypeMarker,
    val position: ConstraintPosition
) : ConstraintSystemCallDiagnostic(INAPPLICABLE)

class NotEnoughInformationForTypeParameter(val typeVariable: TypeVariableMarker) : ConstraintSystemCallDiagnostic(INAPPLICABLE)

class ConstrainingTypeIsError(
    val typeVariable: TypeVariableMarker,
    val constraintType: KotlinTypeMarker,
    val position: IncorporationConstraintPosition
) : ConstraintSystemCallDiagnostic(INAPPLICABLE)