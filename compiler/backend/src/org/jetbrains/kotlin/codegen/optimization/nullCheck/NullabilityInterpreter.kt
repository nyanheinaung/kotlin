/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.nullCheck

import org.jetbrains.kotlin.codegen.inline.ReifiedTypeInliner
import org.jetbrains.kotlin.codegen.inline.operationKind
import org.jetbrains.kotlin.codegen.optimization.boxing.*
import org.jetbrains.kotlin.codegen.optimization.common.OptimizationBasicInterpreter
import org.jetbrains.kotlin.codegen.optimization.common.StrictBasicValue
import org.jetbrains.kotlin.codegen.pseudoInsns.PseudoInsn
import org.jetbrains.kotlin.codegen.pseudoInsns.isPseudo
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.tree.AbstractInsnNode
import org.jetbrains.org.objectweb.asm.tree.MethodInsnNode
import org.jetbrains.org.objectweb.asm.tree.TypeInsnNode
import org.jetbrains.org.objectweb.asm.tree.analysis.BasicValue

class NullabilityInterpreter(private val generationState: GenerationState) : OptimizationBasicInterpreter() {
    override fun newOperation(insn: AbstractInsnNode): BasicValue? {
        val defaultResult = super.newOperation(insn)
        val resultType = defaultResult?.type

        return when {
            insn.opcode == Opcodes.ACONST_NULL ->
                NullBasicValue
            insn.opcode == Opcodes.NEW ->
                NotNullBasicValue(resultType)
            insn.opcode == Opcodes.LDC && resultType.isReferenceType() ->
                NotNullBasicValue(resultType)
            insn.isUnitInstance() ->
                NotNullBasicValue(resultType)
            else ->
                defaultResult
        }
    }

    private fun Type?.isReferenceType() =
        this?.sort.let { it == Type.OBJECT || it == Type.ARRAY }

    override fun unaryOperation(insn: AbstractInsnNode, value: BasicValue?): BasicValue? {
        val defaultResult = super.unaryOperation(insn, value)
        val resultType = defaultResult?.type

        return when (insn.opcode) {
            Opcodes.CHECKCAST ->
                if (insn.isReifiedSafeAs())
                    StrictBasicValue(resultType)
                else
                    value
            Opcodes.NEWARRAY, Opcodes.ANEWARRAY ->
                NotNullBasicValue(resultType)
            else ->
                defaultResult
        }
    }

    private fun AbstractInsnNode.isReifiedSafeAs(): Boolean {
        val marker = previous as? MethodInsnNode ?: return false
        return ReifiedTypeInliner.isOperationReifiedMarker(marker)
                && marker.operationKind == ReifiedTypeInliner.OperationKind.SAFE_AS
    }

    override fun naryOperation(insn: AbstractInsnNode, values: List<BasicValue>): BasicValue? {
        val defaultResult = super.naryOperation(insn, values)
        val resultType = defaultResult?.type

        return when {
            insn.isBoxing(generationState) ->
                NotNullBasicValue(resultType)
            insn.isIteratorMethodCallOfProgression(values) ->
                ProgressionIteratorBasicValue.byProgressionClassType(values[0].type)
            insn.isNextMethodCallOfProgressionIterator(values) ->
                NotNullBasicValue(resultType)
            insn.isPseudo(PseudoInsn.AS_NOT_NULL) ->
                NotNullBasicValue(values[0].type)
            else ->
                defaultResult
        }
    }

    override fun merge(v: BasicValue, w: BasicValue): BasicValue =
        when {
            v is NullBasicValue && w is NullBasicValue ->
                NullBasicValue
            v is NullBasicValue || w is NullBasicValue ->
                StrictBasicValue.REFERENCE_VALUE
            v is ProgressionIteratorBasicValue && w is ProgressionIteratorBasicValue ->
                mergeNotNullValuesOfSameKind(v, w)
            v is ProgressionIteratorBasicValue && w is NotNullBasicValue ->
                NotNullBasicValue.NOT_NULL_REFERENCE_VALUE
            w is ProgressionIteratorBasicValue && v is NotNullBasicValue ->
                NotNullBasicValue.NOT_NULL_REFERENCE_VALUE
            v is NotNullBasicValue && w is NotNullBasicValue ->
                mergeNotNullValuesOfSameKind(v, w)
            else ->
                super.merge(v, w)
        }

    private fun mergeNotNullValuesOfSameKind(v: StrictBasicValue, w: StrictBasicValue) =
        if (v.type == w.type) v else NotNullBasicValue.NOT_NULL_REFERENCE_VALUE

}


fun TypeInsnNode.getObjectType(): Type =
    Type.getObjectType(desc)

