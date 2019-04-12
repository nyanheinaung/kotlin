/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.load.kotlin.getContainingKotlinJvmBinaryClass
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter
import org.jetbrains.org.objectweb.asm.tree.AbstractInsnNode
import org.jetbrains.org.objectweb.asm.tree.MethodNode

// KLUDGE: Inline suspend function built with compiler version less than 1.1.4/1.2-M1 did not contain proper
// before/after suspension point marks, so we detect those functions here and insert the corresponding marks

fun insertLegacySuspendInlineMarks(node: MethodNode) {
    with(node.instructions) {
        // look for return instruction before the end and insert "afterSuspendMarker" there
        insertBefore(findLastReturn(last) ?: return, produceSuspendMarker(false).instructions)
        // insert "beforeSuspendMarker" at the beginning
        insertBefore(first, produceSuspendMarker(true).instructions)
    }
    node.maxStack = node.maxStack.coerceAtLeast(2) // min stack need for suspend marker before return
}

fun findLastReturn(node: AbstractInsnNode?): AbstractInsnNode? {
    var cur = node
    while (cur != null && cur.opcode != Opcodes.ARETURN) cur = cur.previous
    return cur
}

private fun produceSuspendMarker(isStartNotEnd: Boolean): MethodNode =
    MethodNode().also { addSuspendMarker(InstructionAdapter(it), isStartNotEnd) }

fun isLegacySuspendInlineFunction(descriptor: CallableMemberDescriptor): Boolean {
    if (descriptor !is FunctionDescriptor) return false
    if (!descriptor.isSuspend || !descriptor.isInline) return false
    val jvmBytecodeVersion = descriptor.getContainingKotlinJvmBinaryClass()?.classHeader?.bytecodeVersion ?: return false
    return !jvmBytecodeVersion.isAtLeast(1, 0, 2)
}
