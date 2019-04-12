/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.stepping

import com.intellij.debugger.engine.RequestHint
import com.intellij.debugger.engine.SuspendContextImpl
import com.intellij.debugger.engine.evaluation.EvaluateException
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl
import com.intellij.openapi.diagnostic.Logger
import com.sun.jdi.VMDisconnectedException
import com.sun.jdi.request.StepRequest

// Originally copied from RequestHint
class KotlinStepOverInlinedLinesHint(
    stepThread: ThreadReferenceProxyImpl,
    suspendContext: SuspendContextImpl,
    methodFilter: KotlinMethodFilter
) : RequestHint(stepThread, suspendContext, methodFilter) {

    private val LOG = Logger.getInstance(KotlinStepOverInlinedLinesHint::class.java)

    private val filter = methodFilter

    override fun getDepth(): Int = StepRequest.STEP_OVER

    override fun getNextStepDepth(context: SuspendContextImpl): Int {
        try {
            val frameProxy = context.frameProxy
            if (frameProxy != null) {
                if (isTheSameFrame(context)) {
                    return if (filter.locationMatches(context, frameProxy.location())) {
                        STOP
                    } else {
                        StepRequest.STEP_OVER
                    }
                }

                if (isSteppedOut) {
                    return STOP
                }

                return StepRequest.STEP_OUT
            }
        } catch (ignored: VMDisconnectedException) {
        } catch (e: EvaluateException) {
            LOG.error(e)
        }

        return STOP
    }
}