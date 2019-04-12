/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.stepping;

import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.debugger.engine.RequestHint;
import com.intellij.debugger.engine.SuspendContextImpl;
import com.intellij.debugger.jdi.ThreadReferenceProxyImpl;
import com.sun.jdi.request.StepRequest;
import org.jetbrains.annotations.NotNull;

public class DebugProcessImplHelper {
    public static DebugProcessImpl.StepOverCommand createStepOverCommandWithCustomFilter(
            SuspendContextImpl suspendContext,
            boolean ignoreBreakpoints,
            KotlinSuspendCallStepOverFilter methodFilter
    ) {
        DebugProcessImpl debugProcess = suspendContext.getDebugProcess();
        return debugProcess.new StepOverCommand(suspendContext, ignoreBreakpoints, StepRequest.STEP_LINE) {
            @NotNull
            @Override
            protected RequestHint getHint(SuspendContextImpl suspendContext, ThreadReferenceProxyImpl stepThread) {
                @SuppressWarnings("MagicConstant")
                RequestHint hint = new RequestHintWithMethodFilter(stepThread, suspendContext, StepRequest.STEP_OVER, methodFilter);
                hint.setRestoreBreakpoints(ignoreBreakpoints);
                hint.setIgnoreFilters(ignoreBreakpoints || debugProcess.getSession().shouldIgnoreSteppingFilters());

                return hint;
            }
        };
    }
}
