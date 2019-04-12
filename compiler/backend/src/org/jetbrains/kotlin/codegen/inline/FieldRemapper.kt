/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.kotlin.codegen.optimization.common.InsnSequence
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.tree.AbstractInsnNode
import org.jetbrains.org.objectweb.asm.tree.FieldInsnNode
import org.jetbrains.org.objectweb.asm.tree.MethodNode

open class FieldRemapper(
    val originalLambdaInternalName: String?,
    @JvmField val parent: FieldRemapper?,
    protected val parameters: Parameters
) {
    val isRoot = parent == null

    open val isInsideInliningLambda: Boolean = parent?.isInsideInliningLambda ?: false

    protected open fun canProcess(fieldOwner: String, fieldName: String, isFolding: Boolean): Boolean {
        return fieldOwner == originalLambdaInternalName &&
                //don't process general field of anonymous objects
                isCapturedFieldName(fieldName)
    }

    fun foldFieldAccessChainIfNeeded(capturedFieldAccess: List<AbstractInsnNode>, node: MethodNode): AbstractInsnNode? =
        if (capturedFieldAccess.size == 1)
            null //single aload
        else
            foldFieldAccessChainIfNeeded(capturedFieldAccess, 1, node)

    /**constructors could access outer not through
     * ALOAD 0 //this
     * GETFIELD this$0 //outer
     * GETFIELD this$0 //outer of outer
     * but directly through constructor parameter
     * ALOAD X //outer
     * GETFIELD this$0 //outer of outer
     */
    open fun shouldProcessNonAload0FieldAccessChains(): Boolean = false

    private fun foldFieldAccessChainIfNeeded(
        capturedFieldAccess: List<AbstractInsnNode>,
        currentInstruction: Int,
        node: MethodNode
    ): AbstractInsnNode? {
        if (currentInstruction < capturedFieldAccess.lastIndex) {
            //try to fold longest chain first
            parent?.foldFieldAccessChainIfNeeded(capturedFieldAccess, currentInstruction + 1, node)?.let {
                return@foldFieldAccessChainIfNeeded it
            }
        }

        val insnNode = capturedFieldAccess[currentInstruction] as FieldInsnNode
        if (canProcess(insnNode.owner, insnNode.name, true)) {
            insnNode.name = Companion.foldName(getFieldNameForFolding(insnNode))
            insnNode.opcode = Opcodes.GETSTATIC

            node.remove(InsnSequence(capturedFieldAccess[0], insnNode))
            return capturedFieldAccess[capturedFieldAccess.size - 1]
        }

        return null
    }

    protected open fun getFieldNameForFolding(insnNode: FieldInsnNode): String = insnNode.name

    @JvmOverloads
    open fun findField(fieldInsnNode: FieldInsnNode, captured: Collection<CapturedParamInfo> = parameters.captured): CapturedParamInfo? {
        for (valueDescriptor in captured) {
            if (valueDescriptor.originalFieldName == fieldInsnNode.name && valueDescriptor.containingLambdaName == fieldInsnNode.owner) {
                return valueDescriptor
            }
        }
        return null
    }

    open val newLambdaInternalName: String
        get() = originalLambdaInternalName!!

    open fun getFieldForInline(node: FieldInsnNode, prefix: StackValue?): StackValue? =
        MethodInliner.findCapturedField(node, this).remapValue

    companion object {
        fun foldName(fieldName: String) =
            CAPTURED_FIELD_FOLD_PREFIX + fieldName
    }
}
