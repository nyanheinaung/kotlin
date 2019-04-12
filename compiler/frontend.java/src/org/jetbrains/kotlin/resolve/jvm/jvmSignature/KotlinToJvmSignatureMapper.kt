/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.jvmSignature

import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.org.objectweb.asm.commons.Method

interface KotlinToJvmSignatureMapper {
    fun mapToJvmMethodSignature(function: FunctionDescriptor): Method
}

fun erasedSignaturesEqualIgnoringReturnTypes(subFunction: Method, superFunction: Method) =
        subFunction.parametersDescriptor() == superFunction.parametersDescriptor()

private fun Method.parametersDescriptor() = descriptor.substring(1, descriptor.lastIndexOf(")"))
