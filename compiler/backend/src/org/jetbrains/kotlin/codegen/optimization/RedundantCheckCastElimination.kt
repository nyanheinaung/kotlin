/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization

import org.jetbrains.kotlin.codegen.inline.ReifiedTypeInliner
import org.jetbrains.kotlin.codegen.optimization.common.OptimizationBasicInterpreter
import org.jetbrains.kotlin.codegen.optimization.fixStack.top
import org.jetbrains.kotlin.codegen.optimization.transformer.MethodTransformer
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type
import org.jetbrains.org.objectweb.asm.tree.MethodNode
import org.jetbrains.org.objectweb.asm.tree.TypeInsnNode

class RedundantCheckCastEliminationMethodTransformer : MethodTransformer() {
    override fun transform(internalClassName: String, methodNode: MethodNode) {
        val insns = methodNode.instructions.toArray()
        if (!insns.any { it.opcode == Opcodes.CHECKCAST }) return

        val redundantCheckCasts = ArrayList<TypeInsnNode>()

        val frames = analyze(internalClassName, methodNode, OptimizationBasicInterpreter())
        for (i in insns.indices) {
            val valueType = frames[i]?.top()?.type ?: continue
            val insn = insns[i]
            if (ReifiedTypeInliner.isOperationReifiedMarker(insn.previous)) continue

            if (insn is TypeInsnNode) {
                val insnType = Type.getObjectType(insn.desc)
                if (!isTrivialSubtype(insnType, valueType)) continue

                //Keep casts to multiarray types cause dex doesn't recognize ANEWARRAY [Ljava/lang/Object; as Object [][], but Object [] type
                //It's not clear is it bug in dex or not and maybe best to distinguish such types from MULTINEWARRRAY ones in method analyzer
                if (isMultiArrayType(insnType)) continue

                if (insn.opcode == Opcodes.CHECKCAST) {
                    redundantCheckCasts.add(insn)
                }
            }
        }

        redundantCheckCasts.forEach {
            methodNode.instructions.remove(it)
        }
    }

    private fun isTrivialSubtype(superType: Type, subType: Type) =
        superType == subType

    private fun isMultiArrayType(type: Type) = type.sort == Type.ARRAY && type.dimensions != 1
}