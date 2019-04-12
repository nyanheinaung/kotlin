/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.boxing

import org.jetbrains.kotlin.codegen.optimization.common.findPreviousOrNull
import org.jetbrains.kotlin.codegen.optimization.transformer.MethodTransformer
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.tree.AbstractInsnNode
import org.jetbrains.org.objectweb.asm.tree.InsnList
import org.jetbrains.org.objectweb.asm.tree.InsnNode
import org.jetbrains.org.objectweb.asm.tree.MethodNode

class StackPeepholeOptimizationsTransformer : MethodTransformer() {
    override fun transform(internalClassName: String, methodNode: MethodNode) {
        while (transformOnce(methodNode)) {
        }
    }

    private fun transformOnce(methodNode: MethodNode): Boolean {
        val actions = ArrayList<(InsnList) -> Unit>()

        val insns = methodNode.instructions.toArray()

        forInsn@ for (i in 1 until insns.size) {
            val insn = insns[i]
            val prev = insn.previous
            val prevNonNop = insn.findPreviousOrNull { it.opcode != Opcodes.NOP } ?: continue@forInsn

            when (insn.opcode) {
                Opcodes.POP -> {
                    when {
                        prevNonNop.isEliminatedByPop() -> actions.add {
                            it.set(insn, InsnNode(Opcodes.NOP))
                            it.remove(prevNonNop)
                        }
                        prevNonNop.opcode == Opcodes.DUP_X1 -> actions.add {
                            it.remove(insn)
                            it.set(prevNonNop, InsnNode(Opcodes.SWAP))
                        }
                    }
                }

                Opcodes.SWAP -> {
                    val prevNonNop2 = prevNonNop.findPreviousOrNull { it.opcode != Opcodes.NOP } ?: continue@forInsn
                    if (prevNonNop.isPurePushOfSize1() && prevNonNop2.isPurePushOfSize1()) {
                        actions.add {
                            it.remove(insn)
                            it.set(prevNonNop, prevNonNop2.clone(emptyMap()))
                            it.set(prevNonNop2, prevNonNop.clone(emptyMap()))
                        }
                    }
                }

                Opcodes.I2L -> {
                    when (prevNonNop.opcode) {
                        Opcodes.ICONST_0 -> actions.add {
                            it.remove(insn)
                            it.set(prevNonNop, InsnNode(Opcodes.LCONST_0))
                        }
                        Opcodes.ICONST_1 -> actions.add {
                            it.remove(insn)
                            it.set(prevNonNop, InsnNode(Opcodes.LCONST_1))
                        }
                    }
                }

                Opcodes.POP2 -> {
                    if (prevNonNop.isEliminatedByPop2()) {
                        actions.add {
                            it.set(insn, InsnNode(Opcodes.NOP))
                            it.remove(prevNonNop)
                        }
                    } else if (i > 1) {
                        val prevNonNop2 = prevNonNop.findPreviousOrNull { it.opcode != Opcodes.NOP } ?: continue@forInsn
                        if (prevNonNop.isEliminatedByPop() && prevNonNop2.isEliminatedByPop()) {
                            actions.add {
                                it.set(insn, InsnNode(Opcodes.NOP))
                                it.remove(prevNonNop)
                                it.remove(prevNonNop2)
                            }
                        }
                    }
                }

                Opcodes.NOP ->
                    if (prev.opcode == Opcodes.NOP) {
                        actions.add {
                            it.remove(prev)
                        }
                    }
            }
        }

        actions.forEach { it(methodNode.instructions) }

        return actions.isNotEmpty()
    }

    private fun AbstractInsnNode.isEliminatedByPop() =
        isPurePushOfSize1() ||
                opcode == Opcodes.DUP

    private fun AbstractInsnNode.isPurePushOfSize1(): Boolean =
        opcode in Opcodes.ACONST_NULL..Opcodes.FCONST_2 ||
                opcode in Opcodes.BIPUSH..Opcodes.ILOAD ||
                opcode == Opcodes.FLOAD ||
                opcode == Opcodes.ALOAD ||
                isUnitInstance()

    private fun AbstractInsnNode.isEliminatedByPop2() =
        isPurePushOfSize2() ||
                opcode == Opcodes.DUP2

    private fun AbstractInsnNode.isPurePushOfSize2(): Boolean =
        opcode == Opcodes.LCONST_0 || opcode == Opcodes.LCONST_1 ||
                opcode == Opcodes.DCONST_0 || opcode == Opcodes.DCONST_1 ||
                opcode == Opcodes.LLOAD ||
                opcode == Opcodes.DLOAD
}

