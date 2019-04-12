/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.tower

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo

private val INAPPLICABLE_STATUSES = setOf(
    ResolutionCandidateApplicability.INAPPLICABLE,
    ResolutionCandidateApplicability.INAPPLICABLE_ARGUMENTS_MAPPING_ERROR,
    ResolutionCandidateApplicability.INAPPLICABLE_WRONG_RECEIVER
)

val ResolutionCandidateApplicability.isSuccess: Boolean
    get() = this <= ResolutionCandidateApplicability.RESOLVED_LOW_PRIORITY

val CallableDescriptor.isSynthesized: Boolean
    get() = (this is CallableMemberDescriptor && kind == CallableMemberDescriptor.Kind.SYNTHESIZED)

val CandidateWithBoundDispatchReceiver.requiresExtensionReceiver: Boolean
    get() = descriptor.extensionReceiverParameter != null

val ResolutionCandidateApplicability.isInapplicable: Boolean
    get() = this in INAPPLICABLE_STATUSES

fun <C : Candidate> C.forceResolution(): C {
    resultingApplicability
    return this
}