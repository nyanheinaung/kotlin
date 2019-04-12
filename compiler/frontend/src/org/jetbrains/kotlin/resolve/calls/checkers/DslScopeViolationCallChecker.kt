/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.resolve.calls.DslMarkerUtils.extractDslMarkerFqNames
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.resolvedCallUtil.getImplicitReceivers
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.resolve.scopes.utils.parentsWithSelf

object DslScopeViolationCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        if (!context.languageVersionSettings.supportsFeature(LanguageFeature.DslMarkersSupport)) return
        val callImplicitReceivers = resolvedCall.getImplicitReceivers()

        for (callImplicitReceiver in callImplicitReceivers) {
            checkCallImplicitReceiver(callImplicitReceiver, resolvedCall, reportOn, context)
        }
    }

    private fun checkCallImplicitReceiver(
        callImplicitReceiver: ReceiverValue,
        resolvedCall: ResolvedCall<*>,
        reportOn: PsiElement,
        context: CallCheckerContext
    ) {
        val receiversUntilOneFromTheCall =
            context.scope.parentsWithSelf
                .mapNotNull { (it as? LexicalScope)?.implicitReceiver?.value }
                .takeWhile { it != callImplicitReceiver }.toList()

        if (receiversUntilOneFromTheCall.isEmpty()) return

        val (callDslMarkers, additionalCallDslMarkers) = extractDslMarkerFqNames(callImplicitReceiver)
        if (callDslMarkers.isEmpty() && additionalCallDslMarkers.isEmpty()) return

        val dslMarkersFromOuterReceivers = receiversUntilOneFromTheCall.map(::extractDslMarkerFqNames)

        val closestAnotherReceiverWithSameDslMarker =
            dslMarkersFromOuterReceivers.firstOrNull { (dslMarkersFromReceiver, _) ->
                dslMarkersFromReceiver.any(callDslMarkers::contains)
            }

        if (closestAnotherReceiverWithSameDslMarker != null) {
            // TODO: report receivers configuration (what's one is used and what's one is the closest)
            context.trace.report(Errors.DSL_SCOPE_VIOLATION.on(reportOn, resolvedCall.resultingDescriptor))
            return
        }

        val allDslMarkersFromCall = callDslMarkers + additionalCallDslMarkers

        val closestAnotherReceiverWithSameDslMarkerWithDeprecation =
            dslMarkersFromOuterReceivers.firstOrNull { (dslMarkersFromReceiver, additionalDslMarkersFromReceiver) ->
                val allMarkersFromReceiver = dslMarkersFromReceiver + additionalDslMarkersFromReceiver
                allDslMarkersFromCall.any(allMarkersFromReceiver::contains)
            }

        if (closestAnotherReceiverWithSameDslMarkerWithDeprecation != null) {
            val diagnostic =
                if (context.languageVersionSettings.supportsFeature(LanguageFeature.DslMarkerOnFunctionTypeReceiver))
                    Errors.DSL_SCOPE_VIOLATION
                else
                    Errors.DSL_SCOPE_VIOLATION_WARNING

            context.trace.report(diagnostic.on(reportOn, resolvedCall.resultingDescriptor))
        }
    }
}
