/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.tower

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.DeclarationDescriptorWithVisibility
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.calls.model.DiagnosticReporter
import org.jetbrains.kotlin.resolve.calls.model.KotlinCallDiagnostic
import org.jetbrains.kotlin.resolve.calls.tower.ResolutionCandidateApplicability.*
import org.jetbrains.kotlin.resolve.scopes.*
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.KotlinType

interface ImplicitScopeTower {
    val lexicalScope: LexicalScope

    fun getImplicitReceiver(scope: LexicalScope): ReceiverValueWithSmartCastInfo?

    val dynamicScope: MemberScope

    val syntheticScopes: SyntheticScopes

    val location: LookupLocation

    val isDebuggerContext: Boolean

    val isNewInferenceEnabled: Boolean
}

interface ScopeTowerLevel {
    fun getVariables(name: Name, extensionReceiver: ReceiverValueWithSmartCastInfo?): Collection<CandidateWithBoundDispatchReceiver>

    fun getObjects(name: Name, extensionReceiver: ReceiverValueWithSmartCastInfo?): Collection<CandidateWithBoundDispatchReceiver>

    fun getFunctions(name: Name, extensionReceiver: ReceiverValueWithSmartCastInfo?): Collection<CandidateWithBoundDispatchReceiver>

    fun recordLookup(name: Name)
}

class CandidateWithBoundDispatchReceiver(
    val dispatchReceiver: ReceiverValueWithSmartCastInfo?,
    val descriptor: CallableDescriptor,
    val diagnostics: List<ResolutionDiagnostic>
)

fun getResultApplicability(diagnostics: Collection<KotlinCallDiagnostic>) =
    diagnostics.maxBy { it.candidateApplicability }?.candidateApplicability
            ?: RESOLVED

enum class ResolutionCandidateApplicability {
    RESOLVED, // call success or has uncompleted inference or in other words possible successful candidate
    RESOLVED_LOW_PRIORITY,
    CONVENTION_ERROR, // missing infix, operator etc
    MAY_THROW_RUNTIME_ERROR, // unsafe call or unstable smart cast
    RUNTIME_ERROR, // problems with visibility
    IMPOSSIBLE_TO_GENERATE, // access to outer class from nested
    INAPPLICABLE, // arguments have wrong types
    INAPPLICABLE_ARGUMENTS_MAPPING_ERROR, // arguments not mapped to parameters (i.e. different size of arguments and parameters)
    INAPPLICABLE_WRONG_RECEIVER, // receiver not matched
    HIDDEN, // removed from resolve
}

abstract class ResolutionDiagnostic(candidateApplicability: ResolutionCandidateApplicability) :
    KotlinCallDiagnostic(candidateApplicability) {
    override fun report(reporter: DiagnosticReporter) {
        // do nothing
    }
}

// todo error for this access from nested class
class VisibilityError(val invisibleMember: DeclarationDescriptorWithVisibility) : ResolutionDiagnostic(RUNTIME_ERROR) {
    override fun report(reporter: DiagnosticReporter) {
        reporter.onCall(this)
    }
}

class NestedClassViaInstanceReference(val classDescriptor: ClassDescriptor) : ResolutionDiagnostic(IMPOSSIBLE_TO_GENERATE)
class InnerClassViaStaticReference(val classDescriptor: ClassDescriptor) : ResolutionDiagnostic(IMPOSSIBLE_TO_GENERATE)
class UnsupportedInnerClassCall(val message: String) : ResolutionDiagnostic(IMPOSSIBLE_TO_GENERATE)
class UsedSmartCastForDispatchReceiver(val smartCastType: KotlinType) : ResolutionDiagnostic(RESOLVED)

object ErrorDescriptorDiagnostic : ResolutionDiagnostic(RESOLVED) // todo discuss and change to INAPPLICABLE
object LowPriorityDescriptorDiagnostic : ResolutionDiagnostic(RESOLVED_LOW_PRIORITY)
object DynamicDescriptorDiagnostic : ResolutionDiagnostic(RESOLVED_LOW_PRIORITY)
object UnstableSmartCastDiagnostic : ResolutionDiagnostic(MAY_THROW_RUNTIME_ERROR)
object HiddenExtensionRelatedToDynamicTypes : ResolutionDiagnostic(HIDDEN)
object HiddenDescriptor : ResolutionDiagnostic(HIDDEN)

object InvokeConventionCallNoOperatorModifier : ResolutionDiagnostic(CONVENTION_ERROR)
object InfixCallNoInfixModifier : ResolutionDiagnostic(CONVENTION_ERROR)
object DeprecatedUnaryPlusAsPlus : ResolutionDiagnostic(CONVENTION_ERROR)

class ResolvedUsingDeprecatedVisibility(val baseSourceScope: ResolutionScope, val lookupLocation: LookupLocation) : ResolutionDiagnostic(RESOLVED)