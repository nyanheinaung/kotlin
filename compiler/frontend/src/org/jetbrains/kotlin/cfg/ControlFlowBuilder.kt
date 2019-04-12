/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cfg

import org.jetbrains.kotlin.cfg.pseudocode.PseudoValue
import org.jetbrains.kotlin.cfg.pseudocode.Pseudocode
import org.jetbrains.kotlin.cfg.pseudocode.instructions.eval.*
import org.jetbrains.kotlin.contracts.description.InvocationKind
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.constants.CompileTimeConstant
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue

interface ControlFlowBuilder {
    // Subroutines
    fun enterSubroutine(subroutine: KtElement, invocationKind: InvocationKind? = null)

    fun exitSubroutine(subroutine: KtElement, invocationKind: InvocationKind? = null): Pseudocode

    val currentSubroutine: KtElement
    val returnSubroutine: KtElement

    // Scopes
    fun enterBlockScope(block: KtElement)

    fun exitBlockScope(block: KtElement)

    fun getSubroutineExitPoint(labelElement: KtElement): Label?
    fun getLoopConditionEntryPoint(loop: KtLoopExpression): Label?
    fun getLoopExitPoint(loop: KtLoopExpression): Label?

    // Declarations
    fun declareParameter(parameter: KtParameter)

    fun declareVariable(property: KtVariableDeclaration)
    fun declareFunction(subroutine: KtElement, pseudocode: Pseudocode)

    fun declareInlinedFunction(subroutine: KtElement, pseudocode: Pseudocode, invocationKind: InvocationKind)

    fun declareEntryOrObject(entryOrObject: KtClassOrObject)

    // Labels
    fun createUnboundLabel(): Label

    fun createUnboundLabel(name: String): Label

    fun bindLabel(label: Label)

    // Jumps
    fun jump(label: Label, element: KtElement)

    fun jumpOnFalse(label: Label, element: KtElement, conditionValue: PseudoValue?)
    fun jumpOnTrue(label: Label, element: KtElement, conditionValue: PseudoValue?)
    fun nondeterministicJump(label: Label, element: KtElement, inputValue: PseudoValue?)  // Maybe, jump to label
    fun nondeterministicJump(label: List<Label>, element: KtElement)
    fun jumpToError(element: KtElement)

    fun returnValue(returnExpression: KtExpression, returnValue: PseudoValue, subroutine: KtElement)
    fun returnNoValue(returnExpression: KtReturnExpression, subroutine: KtElement)

    fun throwException(throwExpression: KtThrowExpression, thrownValue: PseudoValue)

    // Loops
    fun enterLoop(expression: KtLoopExpression): LoopInfo

    fun enterLoopBody(expression: KtLoopExpression)
    fun exitLoopBody(expression: KtLoopExpression)
    val currentLoop: KtLoopExpression?

    // Try-Finally
    fun enterTryFinally(trigger: GenerationTrigger)

    fun exitTryFinally()

    fun repeatPseudocode(startLabel: Label, finishLabel: Label)

    // Reading values
    fun mark(element: KtElement)

    fun getBoundValue(element: KtElement?): PseudoValue?
    fun bindValue(value: PseudoValue, element: KtElement)
    fun newValue(element: KtElement?): PseudoValue

    fun loadUnit(expression: KtExpression)

    fun loadConstant(expression: KtExpression, constant: CompileTimeConstant<*>?): InstructionWithValue
    fun createAnonymousObject(expression: KtObjectLiteralExpression): InstructionWithValue
    fun createLambda(expression: KtFunction): InstructionWithValue
    fun loadStringTemplate(expression: KtStringTemplateExpression, inputValues: List<PseudoValue>): InstructionWithValue

    fun magic(
        instructionElement: KtElement,
        valueElement: KtElement?,
        inputValues: List<PseudoValue>,
        kind: MagicKind
    ): MagicInstruction

    fun merge(
        expression: KtExpression,
        inputValues: List<PseudoValue>
    ): MergeInstruction

    fun readVariable(
        expression: KtExpression,
        resolvedCall: ResolvedCall<*>,
        receiverValues: Map<PseudoValue, ReceiverValue>
    ): ReadValueInstruction

    fun call(
        valueElement: KtElement,
        resolvedCall: ResolvedCall<*>,
        receiverValues: Map<PseudoValue, ReceiverValue>,
        arguments: Map<PseudoValue, ValueParameterDescriptor>
    ): CallInstruction

    enum class PredefinedOperation {
        AND,
        OR,
        NOT_NULL_ASSERTION
    }

    fun predefinedOperation(
        expression: KtExpression,
        operation: PredefinedOperation,
        inputValues: List<PseudoValue>
    ): OperationInstruction

    fun read(element: KtElement, target: AccessTarget, receiverValues: Map<PseudoValue, ReceiverValue>): ReadValueInstruction

    fun write(
        assignment: KtElement,
        lValue: KtElement,
        rValue: PseudoValue,
        target: AccessTarget,
        receiverValues: Map<PseudoValue, ReceiverValue>
    )
}