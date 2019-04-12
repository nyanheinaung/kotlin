/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.checkers

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.checkers.CheckerContext
import org.jetbrains.kotlin.resolve.deprecation.DeprecationResolver
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.types.DeferredType
import org.jetbrains.kotlin.types.KotlinType

interface CallChecker {
    /**
     * Note that [reportOn] should only be used as a target element for diagnostics reported by checkers.
     * Logic of the checker should not depend on what element is the target of the diagnostic!
     */
    fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext)
}

class CallCheckerContext @JvmOverloads constructor(
    val resolutionContext: ResolutionContext<*>,
    override val deprecationResolver: DeprecationResolver,
    override val moduleDescriptor: ModuleDescriptor,
    override val trace: BindingTrace = resolutionContext.trace
) : CheckerContext {
    val scope: LexicalScope
        get() = resolutionContext.scope

    val dataFlowInfo: DataFlowInfo
        get() = resolutionContext.dataFlowInfo

    val isAnnotationContext: Boolean
        get() = resolutionContext.isAnnotationContext

    val dataFlowValueFactory: DataFlowValueFactory
        get() = resolutionContext.dataFlowValueFactory

    override val languageVersionSettings: LanguageVersionSettings
        get() = resolutionContext.languageVersionSettings
}

// Use this utility to avoid premature computation of deferred return type of a resolved callable descriptor.
// Computing it in CallChecker#check is not feasible since it would trigger "type checking has run into a recursive problem" errors.
// Receiver parameter is present to emphasize that this function should ideally be only used from call checkers.
@Suppress("unused")
fun CallChecker.isComputingDeferredType(type: KotlinType) =
    type is DeferredType && type.isComputing
