/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.kotlin.codegen.AsmUtil
import org.jetbrains.kotlin.codegen.StackValue
import org.jetbrains.org.objectweb.asm.tree.FieldInsnNode

class InlinedLambdaRemapper(
    originalLambdaInternalName: String,
    parent: FieldRemapper,
    methodParams: Parameters,
    private val isDefaultBoundCallableReference: Boolean
) : FieldRemapper(originalLambdaInternalName, parent, methodParams) {

    public override fun canProcess(fieldOwner: String, fieldName: String, isFolding: Boolean) =
        isFolding && (isMyBoundReceiverForDefaultLambda(fieldOwner, fieldName) || super.canProcess(fieldOwner, fieldName, true))

    private fun isMyBoundReceiverForDefaultLambda(fieldOwner: String, fieldName: String) =
        isDefaultBoundCallableReference && fieldName == AsmUtil.BOUND_REFERENCE_RECEIVER && fieldOwner == originalLambdaInternalName

    override fun getFieldNameForFolding(insnNode: FieldInsnNode): String =
        if (isMyBoundReceiverForDefaultLambda(insnNode.owner, insnNode.name)) AsmUtil.RECEIVER_PARAMETER_NAME else insnNode.name

    override fun findField(fieldInsnNode: FieldInsnNode, captured: Collection<CapturedParamInfo>) =
        parent!!.findField(fieldInsnNode, captured)

    override val isInsideInliningLambda: Boolean = true

    override fun getFieldForInline(node: FieldInsnNode, prefix: StackValue?) =
        if (parent!!.isRoot)
            super.getFieldForInline(node, prefix)
        else
            parent.getFieldForInline(node, prefix)
}
