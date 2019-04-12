/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.context.CodegenContext
import org.jetbrains.kotlin.codegen.context.InlineLambdaContext
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.inline.InlineUtil
import org.jetbrains.org.objectweb.asm.Type

class SamWrapperClasses(private val state: GenerationState) {

    private data class WrapperKey(val samType: SamType, val file: KtFile, val insideInline: Boolean)

    private val samInterfaceToWrapperClass = hashMapOf<WrapperKey, Type>()

    fun getSamWrapperClass(
        samType: SamType,
        file: KtFile,
        expressionCodegen: ExpressionCodegen,
        contextDescriptor: CallableMemberDescriptor
    ): Type {
        val isInsideInline = InlineUtil.isInlineOrContainingInline(expressionCodegen.context.contextDescriptor) ||
                isInsideInlineLambdaContext(expressionCodegen.context, state)
        return samInterfaceToWrapperClass.getOrPut(WrapperKey(samType, file, isInsideInline)) {
            SamWrapperCodegen(state, samType, expressionCodegen.parentCodegen, isInsideInline).genWrapper(file, contextDescriptor)
        }
    }

    private fun isInsideInlineLambdaContext(context: CodegenContext<*>, state: GenerationState):Boolean {
        var parent: CodegenContext<*>? = context
        while (parent != null && parent != state.rootContext) {
            if (parent is InlineLambdaContext) return true
            parent = parent.parentContext
        }
        return false
    }
}
