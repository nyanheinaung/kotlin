/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.pseudoInsns

import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import org.jetbrains.org.objectweb.asm.tree.AbstractInsnNode
import org.jetbrains.org.objectweb.asm.tree.InsnList
import org.jetbrains.org.objectweb.asm.tree.MethodInsnNode

val PSEUDO_INSN_CALL_OWNER: String = "kotlin/jvm/internal/\$PseudoInsn"

enum class PseudoInsn(val signature: String = "()V") {
    FIX_STACK_BEFORE_JUMP,
    FAKE_ALWAYS_TRUE_IFEQ("()I"),
    FAKE_ALWAYS_FALSE_IFEQ("()I"),
    SAVE_STACK_BEFORE_TRY,
    RESTORE_STACK_IN_TRY_CATCH,
    STORE_NOT_NULL,
    AS_NOT_NULL("(Ljava/lang/Object;)Ljava/lang/Object;")
    ;

    fun emit(iv: InstructionAdapter) {
        iv.invokestatic(PSEUDO_INSN_CALL_OWNER, toString(), signature, false)
    }

    fun createInsnNode(): MethodInsnNode =
            MethodInsnNode(Opcodes.INVOKESTATIC, PSEUDO_INSN_CALL_OWNER, toString(), signature, false)

    fun isa(node: AbstractInsnNode): Boolean =
            this == parsePseudoInsnOrNull(node)
}

fun isPseudoInsn(insn: AbstractInsnNode): Boolean =
        insn is MethodInsnNode && insn.getOpcode() == Opcodes.INVOKESTATIC && insn.owner == PSEUDO_INSN_CALL_OWNER

fun parsePseudoInsnOrNull(insn: AbstractInsnNode): PseudoInsn? =
        if (isPseudoInsn(insn))
            PseudoInsn.valueOf((insn as MethodInsnNode).name)
        else null

fun InstructionAdapter.fixStackAndJump(label: Label) {
    PseudoInsn.FIX_STACK_BEFORE_JUMP.emit(this)
    this.goTo(label)
}

fun InstructionAdapter.fakeAlwaysTrueIfeq(label: Label) {
    PseudoInsn.FAKE_ALWAYS_TRUE_IFEQ.emit(this)
    this.ifeq(label)
}

fun InstructionAdapter.fakeAlwaysFalseIfeq(label: Label) {
    PseudoInsn.FAKE_ALWAYS_FALSE_IFEQ.emit(this)
    this.ifeq(label)
}

fun InstructionAdapter.storeNotNull() {
    PseudoInsn.STORE_NOT_NULL.emit(this)
}

fun InstructionAdapter.asNotNull() {
    PseudoInsn.AS_NOT_NULL.emit(this)
}

fun AbstractInsnNode.isPseudo(pseudoInsn: PseudoInsn) =
        pseudoInsn.isa(this)