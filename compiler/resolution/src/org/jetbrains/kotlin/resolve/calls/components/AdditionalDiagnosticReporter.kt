/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.components

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ReceiverParameterDescriptor
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.types.UnwrappedType
import org.jetbrains.kotlin.types.checker.KotlinTypeChecker

// very initial state of component
// todo: handle all diagnostic inside DiagnosticReporterByTrackingStrategy
// move it to frontend module
class AdditionalDiagnosticReporter(private val languageVersionSettings: LanguageVersionSettings) {

    fun reportAdditionalDiagnostics(
        candidate: ResolvedCallAtom,
        resultingDescriptor: CallableDescriptor,
        kotlinDiagnosticsHolder: KotlinDiagnosticsHolder,
        diagnostics: Collection<KotlinCallDiagnostic>
    ) {
        reportSmartCasts(candidate, resultingDescriptor, kotlinDiagnosticsHolder, diagnostics)
    }

    private fun createSmartCastDiagnostic(
        candidate: ResolvedCallAtom,
        argument: KotlinCallArgument,
        expectedResultType: UnwrappedType
    ): SmartCastDiagnostic? {
        if (argument !is ExpressionKotlinCallArgument) return null
        if (!KotlinTypeChecker.DEFAULT.isSubtypeOf(argument.receiver.receiverValue.type, expectedResultType)) {
            return SmartCastDiagnostic(argument, expectedResultType.unwrap(), candidate.atom)
        }
        return null
    }

    private fun reportSmartCastOnReceiver(
        candidate: ResolvedCallAtom,
        receiver: SimpleKotlinCallArgument?,
        parameter: ReceiverParameterDescriptor?,
        diagnostics: Collection<KotlinCallDiagnostic>
    ): SmartCastDiagnostic? {
        if (receiver == null || parameter == null) return null
        val expectedType = parameter.type.unwrap().let { if (receiver.isSafeCall) it.makeNullableAsSpecified(true) else it }

        val smartCastDiagnostic = createSmartCastDiagnostic(candidate, receiver, expectedType) ?: return null

        // todo may be we have smart cast to Int?
        return smartCastDiagnostic.takeIf {
            diagnostics.filterIsInstance<UnsafeCallError>().none {
                it.receiver == receiver
            }
                    &&
                    diagnostics.filterIsInstance<UnstableSmartCast>().none {
                        it.argument == receiver
                    }
        }
    }

    private fun reportSmartCasts(
        candidate: ResolvedCallAtom,
        resultingDescriptor: CallableDescriptor,
        kotlinDiagnosticsHolder: KotlinDiagnosticsHolder,
        diagnostics: Collection<KotlinCallDiagnostic>
    ) {
        kotlinDiagnosticsHolder.addDiagnosticIfNotNull(
            reportSmartCastOnReceiver(
                candidate,
                candidate.extensionReceiverArgument,
                resultingDescriptor.extensionReceiverParameter,
                diagnostics
            )
        )
        kotlinDiagnosticsHolder.addDiagnosticIfNotNull(
            reportSmartCastOnReceiver(
                candidate,
                candidate.dispatchReceiverArgument,
                resultingDescriptor.dispatchReceiverParameter,
                diagnostics
            )
        )

        for (parameter in resultingDescriptor.valueParameters) {
            for (argument in candidate.argumentMappingByOriginal[parameter.original]?.arguments ?: continue) {
                val effectiveExpectedType = argument.getExpectedType(parameter, languageVersionSettings)
                val smartCastDiagnostic = createSmartCastDiagnostic(candidate, argument, effectiveExpectedType) ?: continue

                val thereIsUnstableSmartCastError = diagnostics.filterIsInstance<UnstableSmartCast>().any {
                    it.argument == argument
                }

                if (!thereIsUnstableSmartCastError) {
                    kotlinDiagnosticsHolder.addDiagnostic(smartCastDiagnostic)
                }
            }
        }
    }
}