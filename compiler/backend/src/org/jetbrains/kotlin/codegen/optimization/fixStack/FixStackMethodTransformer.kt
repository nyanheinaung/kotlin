/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.fixStack

import org.jetbrains.kotlin.codegen.inline.isAfterInlineMarker
import org.jetbrains.kotlin.codegen.inline.isBeforeInlineMarker
import org.jetbrains.kotlin.codegen.inline.isInlineMarker
import org.jetbrains.kotlin.codegen.optimization.common.InsnSequence
import org.jetbrains.kotlin.codegen.optimization.transformer.MethodTransformer
import org.jetbrains.kotlin.codegen.pseudoInsns.PseudoInsn
import org.jetbrains.kotlin.codegen.pseudoInsns.parsePseudoInsnOrNull
import org.jetbrains.org.objectweb.asm.tree.AbstractInsnNode
import org.jetbrains.org.objectweb.asm.tree.MethodNode

class FixStackMethodTransformer : MethodTransformer() {
    override fun transform(internalClassName: String, methodNode: MethodNode) {
        val context = FixStackContext(methodNode)

        if (!context.hasAnyMarkers()) return

        // If inline method markers are inconsistent, remove them now
        if (!context.consistentInlineMarkers) {
            InsnSequence(methodNode.instructions).forEach { insnNode ->
                if (isInlineMarker(insnNode))
                    methodNode.instructions.remove(insnNode)
            }
        }

        if (context.isAnalysisRequired()) {
            analyzeAndTransformBreakContinueGotos(context, internalClassName, methodNode)
            removeAlwaysFalseIfeqMarkers(context, methodNode)
            analyzeAndTransformSaveRestoreStack(context, internalClassName, methodNode)
        }

        removeAlwaysTrueIfeqMarkers(context, methodNode)
        removeAlwaysFalseIfeqMarkers(context, methodNode)
    }

    private fun analyzeAndTransformBreakContinueGotos(context: FixStackContext, internalClassName: String, methodNode: MethodNode) {
        val analyzer = FixStackAnalyzer(internalClassName, methodNode, context)
        analyzer.analyze()

        methodNode.maxStack = methodNode.maxStack + analyzer.maxExtraStackSize

        val actions = arrayListOf<() -> Unit>()

        transformBreakContinueGotos(methodNode, context, actions, analyzer)

        actions.forEach { it() }
    }

    private fun analyzeAndTransformSaveRestoreStack(context: FixStackContext, internalClassName: String, methodNode: MethodNode) {
        val analyzer = FixStackAnalyzer(internalClassName, methodNode, context, skipBreakContinueGotoEdges = false)
        analyzer.analyze()

        val actions = arrayListOf<() -> Unit>()

        transformSaveRestoreStackMarkers(methodNode, context, actions, analyzer)

        actions.forEach { it() }
    }

    private fun removeAlwaysFalseIfeqMarkers(context: FixStackContext, methodNode: MethodNode) {
        context.fakeAlwaysFalseIfeqMarkers.forEach { marker ->
            removeAlwaysFalseIfeq(methodNode, marker)
        }
        context.fakeAlwaysFalseIfeqMarkers.clear()
    }

    private fun removeAlwaysTrueIfeqMarkers(context: FixStackContext, methodNode: MethodNode) {
        context.fakeAlwaysTrueIfeqMarkers.forEach { marker ->
            replaceAlwaysTrueIfeqWithGoto(methodNode, marker)
        }
        context.fakeAlwaysTrueIfeqMarkers.clear()
    }

    private fun transformBreakContinueGotos(
        methodNode: MethodNode,
        fixStackContext: FixStackContext,
        actions: MutableList<() -> Unit>,
        analyzer: FixStackAnalyzer
    ) {
        fixStackContext.breakContinueGotoNodes.forEach { gotoNode ->
            val gotoIndex = methodNode.instructions.indexOf(gotoNode)
            val labelIndex = methodNode.instructions.indexOf(gotoNode.label)

            val actualStackSize = analyzer.getActualStackSize(gotoNode)
            val expectedStackSize = analyzer.getExpectedStackSize(gotoNode.label)

            if (actualStackSize >= 0 && expectedStackSize >= 0) {
                assert(expectedStackSize <= actualStackSize) {
                    "Label at $labelIndex, jump at $gotoIndex: stack underflow: $expectedStackSize > $actualStackSize"
                }
                val actualStackContent = analyzer.getActualStack(gotoNode)
                        ?: throw AssertionError("Jump at $gotoIndex should be alive")
                actions.add { replaceMarkerWithPops(methodNode, gotoNode.previous, expectedStackSize, actualStackContent) }
            } else if (actualStackSize >= 0 && expectedStackSize < 0) {
                throw AssertionError("Live jump $gotoIndex to dead label $labelIndex")
            } else {
                val marker = gotoNode.previous
                actions.add { methodNode.instructions.remove(marker) }
            }
        }
    }

    private fun transformSaveRestoreStackMarkers(
        methodNode: MethodNode,
        context: FixStackContext,
        actions: MutableList<() -> Unit>,
        analyzer: FixStackAnalyzer
    ) {
        val localVariablesManager = LocalVariablesManager(context, methodNode)
        InsnSequence(methodNode.instructions).forEach { marker ->
            val pseudoInsn = parsePseudoInsnOrNull(marker)
            when {
                pseudoInsn == PseudoInsn.SAVE_STACK_BEFORE_TRY ->
                    transformSaveStackMarker(methodNode, actions, analyzer, marker, localVariablesManager)
                pseudoInsn == PseudoInsn.RESTORE_STACK_IN_TRY_CATCH ->
                    transformRestoreStackMarker(methodNode, actions, marker, localVariablesManager)
                isBeforeInlineMarker(marker) ->
                    transformBeforeInlineCallMarker(methodNode, actions, analyzer, marker, localVariablesManager)
                isAfterInlineMarker(marker) ->
                    transformAfterInlineCallMarker(methodNode, actions, analyzer, marker, localVariablesManager)
            }
        }
    }

    private fun transformSaveStackMarker(
        methodNode: MethodNode,
        actions: MutableList<() -> Unit>,
        analyzer: FixStackAnalyzer,
        marker: AbstractInsnNode,
        localVariablesManager: LocalVariablesManager
    ) {
        val savedStackValues = analyzer.getStackToSpill(marker)
        if (savedStackValues != null) {
            val savedStackDescriptor = localVariablesManager.allocateVariablesForSaveStackMarker(marker, savedStackValues)
            actions.add { saveStack(methodNode, marker, savedStackDescriptor) }
        } else {
            // marker is dead code
            localVariablesManager.allocateVariablesForSaveStackMarker(marker, emptyList())
            actions.add { methodNode.instructions.remove(marker) }
        }
    }

    private fun transformRestoreStackMarker(
        methodNode: MethodNode,
        actions: MutableList<() -> Unit>,
        marker: AbstractInsnNode,
        localVariablesManager: LocalVariablesManager
    ) {
        val savedStackDescriptor = localVariablesManager.getSavedStackDescriptor(marker)
        actions.add { restoreStack(methodNode, marker, savedStackDescriptor) }
        localVariablesManager.markRestoreStackMarkerEmitted(marker)
    }

    private fun transformAfterInlineCallMarker(
        methodNode: MethodNode,
        actions: MutableList<() -> Unit>,
        analyzer: FixStackAnalyzer,
        inlineMarker: AbstractInsnNode,
        localVariablesManager: LocalVariablesManager
    ) {
        val savedStackDescriptor = localVariablesManager.getBeforeInlineDescriptor(inlineMarker)
        val stackContentAfterInline = analyzer.getActualStack(inlineMarker)
        if (stackContentAfterInline != null && savedStackDescriptor.isNotEmpty()) {
            when (stackContentAfterInline.size) {
                1 -> {
                    val returnValue = stackContentAfterInline.last()
                    val returnValueLocalVarIndex = localVariablesManager.createReturnValueVariable(returnValue)
                    actions.add {
                        restoreStackWithReturnValue(
                            methodNode, inlineMarker, savedStackDescriptor,
                            returnValue, returnValueLocalVarIndex
                        )
                    }
                }
                0 ->
                    actions.add { restoreStack(methodNode, inlineMarker, savedStackDescriptor) }
                else ->
                    throw AssertionError("Inline method should not leave more than 1 value on stack")
            }
        } else {
            // after inline marker is dead code
            actions.add { methodNode.instructions.remove(inlineMarker) }
        }
        localVariablesManager.markAfterInlineMarkerEmitted(inlineMarker)
    }

    private fun transformBeforeInlineCallMarker(
        methodNode: MethodNode,
        actions: MutableList<() -> Unit>,
        analyzer: FixStackAnalyzer,
        inlineMarker: AbstractInsnNode,
        localVariablesManager: LocalVariablesManager
    ) {
        val savedStackValues = analyzer.getStackToSpill(inlineMarker)
        if (savedStackValues != null) {
            val savedStackDescriptor = localVariablesManager.allocateVariablesForBeforeInlineMarker(inlineMarker, savedStackValues)
            actions.add { saveStack(methodNode, inlineMarker, savedStackDescriptor) }
        } else {
            // before inline marker is dead code
            localVariablesManager.allocateVariablesForBeforeInlineMarker(inlineMarker, emptyList())
            actions.add { methodNode.instructions.remove(inlineMarker) }
        }
    }


}
