/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls

import org.jetbrains.kotlin.config.LanguageFeature
import org.jetbrains.kotlin.resolve.calls.components.CallableReferenceResolver
import org.jetbrains.kotlin.resolve.calls.components.KotlinCallCompleter
import org.jetbrains.kotlin.resolve.calls.components.KotlinResolutionCallbacks
import org.jetbrains.kotlin.resolve.calls.components.NewOverloadingConflictResolver
import org.jetbrains.kotlin.resolve.calls.context.CheckArgumentTypesMode
import org.jetbrains.kotlin.resolve.calls.model.*
import org.jetbrains.kotlin.resolve.calls.tower.*
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValueWithSmartCastInfo
import org.jetbrains.kotlin.types.UnwrappedType
import java.lang.UnsupportedOperationException


class KotlinCallResolver(
    private val towerResolver: TowerResolver,
    private val kotlinCallCompleter: KotlinCallCompleter,
    private val overloadingConflictResolver: NewOverloadingConflictResolver,
    private val callableReferenceResolver: CallableReferenceResolver,
    private val callComponents: KotlinCallComponents
) {

    fun resolveCall(
        scopeTower: ImplicitScopeTower,
        resolutionCallbacks: KotlinResolutionCallbacks,
        kotlinCall: KotlinCall,
        expectedType: UnwrappedType?,
        collectAllCandidates: Boolean,
        createFactoryProviderForInvoke: () -> CandidateFactoryProviderForInvoke<KotlinResolutionCandidate>
    ): CallResolutionResult {
        kotlinCall.checkCallInvariants()

        val candidateFactory = SimpleCandidateFactory(
            callComponents, scopeTower, kotlinCall, resolutionCallbacks, callableReferenceResolver
        )
        val processor = when (kotlinCall.callKind) {
            KotlinCallKind.VARIABLE -> {
                createVariableAndObjectProcessor(scopeTower, kotlinCall.name, candidateFactory, kotlinCall.explicitReceiver?.receiver)
            }
            KotlinCallKind.FUNCTION -> {
                createFunctionProcessor(
                    scopeTower,
                    kotlinCall.name,
                    candidateFactory,
                    createFactoryProviderForInvoke(),
                    kotlinCall.explicitReceiver?.receiver
                )
            }
            KotlinCallKind.INVOKE -> {
                createProcessorWithReceiverValueOrEmpty(kotlinCall.explicitReceiver?.receiver) {
                    createCallTowerProcessorForExplicitInvoke(
                        scopeTower,
                        candidateFactory,
                        kotlinCall.dispatchReceiverForInvokeExtension?.receiver as ReceiverValueWithSmartCastInfo,
                        it
                    )
                }
            }
            KotlinCallKind.UNSUPPORTED -> throw UnsupportedOperationException()
        }

        if (collectAllCandidates) {
            val allCandidates = towerResolver.collectAllCandidates(scopeTower, processor, kotlinCall.name)
            return kotlinCallCompleter.createAllCandidatesResult(allCandidates, expectedType, resolutionCallbacks)
        }

        val candidates = towerResolver.runResolve(
            scopeTower,
            processor,
            useOrder = kotlinCall.callKind != KotlinCallKind.UNSUPPORTED,
            name = kotlinCall.name
        )

        return choseMostSpecific(candidateFactory, resolutionCallbacks, expectedType, candidates)
    }

    fun resolveGivenCandidates(
        scopeTower: ImplicitScopeTower,
        resolutionCallbacks: KotlinResolutionCallbacks,
        kotlinCall: KotlinCall,
        expectedType: UnwrappedType?,
        givenCandidates: Collection<GivenCandidate>,
        collectAllCandidates: Boolean
    ): CallResolutionResult {
        kotlinCall.checkCallInvariants()
        val candidateFactory = SimpleCandidateFactory(
            callComponents, scopeTower, kotlinCall, resolutionCallbacks, callableReferenceResolver
        )

        val resolutionCandidates = givenCandidates.map { candidateFactory.createCandidate(it).forceResolution() }

        if (collectAllCandidates) {
            val allCandidates = towerResolver.runWithEmptyTowerData(
                KnownResultProcessor(resolutionCandidates),
                TowerResolver.AllCandidatesCollector(),
                useOrder = false
            )
            return kotlinCallCompleter.createAllCandidatesResult(allCandidates, expectedType, resolutionCallbacks)

        }
        val candidates = towerResolver.runWithEmptyTowerData(
            KnownResultProcessor(resolutionCandidates),
            TowerResolver.SuccessfulResultCollector(),
            useOrder = true
        )
        return choseMostSpecific(candidateFactory, resolutionCallbacks, expectedType, candidates)
    }

    private fun choseMostSpecific(
        candidateFactory: SimpleCandidateFactory,
        resolutionCallbacks: KotlinResolutionCallbacks,
        expectedType: UnwrappedType?,
        candidates: Collection<KotlinResolutionCandidate>
    ): CallResolutionResult {
        val isDebuggerContext = candidateFactory.scopeTower.isDebuggerContext

        var refinedCandidates = candidates
        if (!callComponents.languageVersionSettings.supportsFeature(LanguageFeature.RefinedSamAdaptersPriority)) {
            val nonSynthesized = candidates.filter { !it.resolvedCall.candidateDescriptor.isSynthesized }
            if (!nonSynthesized.isEmpty()) {
                refinedCandidates = nonSynthesized
            }
        }

        val maximallySpecificCandidates = overloadingConflictResolver.chooseMaximallySpecificCandidates(
            refinedCandidates,
            CheckArgumentTypesMode.CHECK_VALUE_ARGUMENTS,
            discriminateGenerics = true, // todo
            isDebuggerContext = isDebuggerContext
        )

        return kotlinCallCompleter.runCompletion(candidateFactory, maximallySpecificCandidates, expectedType, resolutionCallbacks)
    }
}

