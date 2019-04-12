/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.codegen.Callable
import org.jetbrains.kotlin.codegen.ExpressionCodegen
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.types.Variance

class IsArrayOf : IntrinsicMethod() {
    override fun toCallable(fd: FunctionDescriptor, isSuper: Boolean, resolvedCall: ResolvedCall<*>, codegen: ExpressionCodegen): Callable {
        val typeArguments = resolvedCall.typeArguments
        assert(typeArguments.size == 1) { "Expected only one type parameter for Any?.isArrayOf(), got: $typeArguments" }

        val typeMapper = codegen.state.typeMapper
        val method = typeMapper.mapToCallableMethod(fd, false)

        val builtIns = fd.module.builtIns
        val elementType = typeArguments.values.first()
        val arrayKtType = builtIns.getArrayType(Variance.INVARIANT, elementType)
        val arrayType = typeMapper.mapType(arrayKtType)

        return createIntrinsicCallable(method) {
            it.instanceOf(arrayType)
        }
    }
}