/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.stepping

import com.intellij.debugger.SourcePosition
import com.intellij.debugger.engine.BreakpointStepMethodFilter
import com.intellij.debugger.engine.DebugProcessImpl
import com.intellij.util.Range
import com.sun.jdi.Location
import org.jetbrains.kotlin.codegen.coroutines.isResumeImplMethodNameFromAnyLanguageSettings
import org.jetbrains.kotlin.idea.debugger.isInsideInlineArgument
import org.jetbrains.kotlin.idea.refactoring.isMultiLine
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.util.OperatorNameConventions

class KotlinLambdaMethodFilter(
    private val lambda: KtFunction,
    private val myCallingExpressionLines: Range<Int>,
    private val isInline: Boolean,
    private val isSuspend: Boolean
) : BreakpointStepMethodFilter {
    private val myFirstStatementPosition: SourcePosition?
    private val myLastStatementLine: Int

    init {
        val body = lambda.bodyExpression
        if (body != null && lambda.isMultiLine()) {
            var firstStatementPosition: SourcePosition? = null
            var lastStatementPosition: SourcePosition? = null
            val statements = (body as? KtBlockExpression)?.statements ?: listOf(body)
            if (statements.isNotEmpty()) {
                firstStatementPosition = SourcePosition.createFromElement(statements.first())
                if (firstStatementPosition != null) {
                    val lastStatement = statements.last()
                    lastStatementPosition = SourcePosition.createFromOffset(firstStatementPosition.file, lastStatement.textRange.endOffset)
                }
            }
            myFirstStatementPosition = firstStatementPosition
            myLastStatementLine = if (lastStatementPosition != null) lastStatementPosition.line else -1
        } else {
            myFirstStatementPosition = SourcePosition.createFromElement(lambda)
            myLastStatementLine = myFirstStatementPosition!!.line
        }
    }

    override fun getBreakpointPosition() = myFirstStatementPosition
    override fun getLastStatementLine() = myLastStatementLine

    override fun locationMatches(process: DebugProcessImpl, location: Location): Boolean {
        val method = location.method()

        if (isInline) {
            return isInsideInlineArgument(lambda, location, process)
        }

        return isLambdaName(method.name())
    }

    override fun getCallingExpressionLines() = if (isInline) Range(0, Int.MAX_VALUE) else myCallingExpressionLines

    private fun isLambdaName(name: String?): Boolean {
        if (isSuspend && name != null) {
            return isResumeImplMethodNameFromAnyLanguageSettings(name)
        }

        return name == OperatorNameConventions.INVOKE.asString()
    }

}
