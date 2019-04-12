/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization

import org.jetbrains.kotlin.codegen.TransformationMethodVisitor
import org.jetbrains.kotlin.codegen.optimization.boxing.PopBackwardPropagationTransformer
import org.jetbrains.kotlin.codegen.optimization.boxing.RedundantBoxingMethodTransformer
import org.jetbrains.kotlin.codegen.optimization.boxing.StackPeepholeOptimizationsTransformer
import org.jetbrains.kotlin.codegen.optimization.common.prepareForEmitting
import org.jetbrains.kotlin.codegen.optimization.nullCheck.RedundantNullCheckMethodTransformer
import org.jetbrains.kotlin.codegen.optimization.transformer.CompositeMethodTransformer
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.tree.MethodNode

class OptimizationMethodVisitor(
    delegate: MethodVisitor,
    private val generationState: GenerationState,
    access: Int,
    name: String,
    desc: String,
    signature: String?,
    exceptions: Array<String>?
) : TransformationMethodVisitor(delegate, access, name, desc, signature, exceptions) {
    private val constructorCallNormalizationTransformer =
        UninitializedStoresMethodTransformer(generationState.constructorCallNormalizationMode)

    val normalizationMethodTransformer = CompositeMethodTransformer(
        FixStackWithLabelNormalizationMethodTransformer(),
        MethodVerifier("AFTER mandatory stack transformations")
    )

    val optimizationTransformer = CompositeMethodTransformer(
        CapturedVarsOptimizationMethodTransformer(),
        RedundantNullCheckMethodTransformer(generationState),
        RedundantCheckCastEliminationMethodTransformer(),
        ConstantConditionEliminationMethodTransformer(),
        RedundantBoxingMethodTransformer(generationState),
        StackPeepholeOptimizationsTransformer(),
        PopBackwardPropagationTransformer(),
        DeadCodeEliminationMethodTransformer(),
        RedundantGotoMethodTransformer(),
        RedundantNopsCleanupMethodTransformer(),
        MethodVerifier("AFTER optimizations")
    )

    override fun performTransformations(methodNode: MethodNode) {
        normalizationMethodTransformer.transform("fake", methodNode)
        constructorCallNormalizationTransformer.transform("fake", methodNode)

        if (canBeOptimized(methodNode) && !generationState.disableOptimization) {
            optimizationTransformer.transform("fake", methodNode)
        }

        DeadCodeEliminationMethodTransformer().transform("fake", methodNode)

        methodNode.prepareForEmitting()
    }

    companion object {
        private val MEMORY_LIMIT_BY_METHOD_MB = 50

        fun canBeOptimized(node: MethodNode): Boolean {
            val totalFramesSizeMb = node.instructions.size() * (node.maxLocals + node.maxStack) / (1024 * 1024)
            return totalFramesSizeMb < MEMORY_LIMIT_BY_METHOD_MB
        }

        fun canBeOptimizedUsingSourceInterpreter(node: MethodNode): Boolean {
            val frameSize = node.maxLocals + node.maxStack
            val methodSize = node.instructions.size().toLong()
            val totalFramesSizeMb = methodSize * methodSize * frameSize / (1024 * 1024)
            return totalFramesSizeMb < MEMORY_LIMIT_BY_METHOD_MB
        }
    }
}
