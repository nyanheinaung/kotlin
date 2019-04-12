/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.diagnostics.DiagnosticSink
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall

class InlineCycleReporter(private val diagnostics: DiagnosticSink) {

    private val processingFunctions = linkedMapOf<PsiElement, CallableDescriptor>()

    fun enterIntoInlining(call: ResolvedCall<*>?): Boolean {
        //null call for default method inlining
        if (call != null) {
            val callElement = call.call.callElement
            if (processingFunctions.contains(callElement)) {
                val cycle = processingFunctions.asSequence().dropWhile { it.key != callElement }
                cycle.forEach {
                    diagnostics.report(Errors.INLINE_CALL_CYCLE.on(it.key, it.value))
                }
                return false
            }
            processingFunctions.put(callElement, call.resultingDescriptor.original)
        }
        return true
    }

    fun exitFromInliningOf(call: ResolvedCall<*>?) {
        if (call != null) {
            val callElement = call.call.callElement
            processingFunctions.remove(callElement)
        }
    }
}
