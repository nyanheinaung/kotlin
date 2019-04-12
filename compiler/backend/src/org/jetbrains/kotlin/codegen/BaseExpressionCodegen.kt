/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import org.jetbrains.kotlin.codegen.inline.NameGenerator
import org.jetbrains.kotlin.codegen.inline.ReifiedTypeParametersUsages
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

interface BaseExpressionCodegen {

    val frameMap: FrameMapBase<*>

    val visitor: InstructionAdapter

    val inlineNameGenerator: NameGenerator

    val lastLineNumber: Int

    fun consumeReifiedOperationMarker(typeParameterDescriptor: TypeParameterDescriptor)

    fun propagateChildReifiedTypeParametersUsages(reifiedTypeParametersUsages: ReifiedTypeParametersUsages)

    fun pushClosureOnStack(
        classDescriptor: ClassDescriptor,
        putThis: Boolean,
        callGenerator: CallGenerator,
        functionReferenceReceiver: StackValue?
    )

    fun markLineNumberAfterInlineIfNeeded()
}
