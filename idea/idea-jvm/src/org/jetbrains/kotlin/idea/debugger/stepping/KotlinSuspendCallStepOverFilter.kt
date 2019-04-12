/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.stepping

import com.intellij.debugger.DebuggerBundle
import com.intellij.debugger.DebuggerManagerEx
import com.intellij.debugger.engine.DebugProcessImpl
import com.intellij.debugger.engine.MethodFilter
import com.intellij.debugger.engine.RequestHint
import com.intellij.debugger.engine.SuspendContextImpl
import com.intellij.debugger.settings.DebuggerSettings
import com.intellij.psi.PsiFile
import com.intellij.util.Range
import com.intellij.xdebugger.impl.XSourcePositionImpl
import com.sun.jdi.Location
import com.sun.jdi.request.EventRequest
import org.jetbrains.kotlin.idea.debugger.isOnSuspendReturnOrReenter
import org.jetbrains.kotlin.idea.debugger.suspendFunctionFirstLineLocation
import org.jetbrains.kotlin.idea.util.application.runReadAction

class KotlinSuspendCallStepOverFilter(
    private val line: Int,
    private val file: PsiFile,
    private val ignoreBreakpoints: Boolean
) : MethodFilter {
    override fun getCallingExpressionLines(): Range<Int>? = Range(line, line)

    override fun locationMatches(process: DebugProcessImpl, location: Location?): Boolean {
        return location != null && isOnSuspendReturnOrReenter(location)
    }

    override fun onReached(context: SuspendContextImpl, hint: RequestHint): Int {
        val location = context.frameProxy?.location() ?: return RequestHint.STOP
        val suspendStartLineNumber = suspendFunctionFirstLineLocation(location) ?: return RequestHint.STOP

        val debugProcess = context.debugProcess
        val breakpointManager = DebuggerManagerEx.getInstanceEx(debugProcess.project).breakpointManager
        breakpointManager.applyThreadFilter(debugProcess, null)

        createRunToCursorBreakpoint(context, suspendStartLineNumber - 1, file, ignoreBreakpoints)
        return RequestHint.RESUME
    }
}

private fun createRunToCursorBreakpoint(context: SuspendContextImpl, line: Int, file: PsiFile, ignoreBreakpoints: Boolean) {
    val position = XSourcePositionImpl.create(file.virtualFile, line) ?: return
    val process = context.debugProcess
    process.showStatusText(DebuggerBundle.message("status.run.to.cursor"))
    process.cancelRunToCursorBreakpoint()

    if (ignoreBreakpoints) {
        DebuggerManagerEx.getInstanceEx(process.project).breakpointManager.disableBreakpoints(process)
    }

    val runToCursorBreakpoint =
        runReadAction {
            DebuggerManagerEx.getInstanceEx(process.project).breakpointManager.addRunToCursorBreakpoint(position, ignoreBreakpoints)
        } ?: return

    runToCursorBreakpoint.suspendPolicy = when {
        context.suspendPolicy == EventRequest.SUSPEND_EVENT_THREAD -> DebuggerSettings.SUSPEND_THREAD
        else -> DebuggerSettings.SUSPEND_ALL
    }

    runToCursorBreakpoint.createRequest(process)
    process.setRunToCursorBreakpoint(runToCursorBreakpoint)
}