/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.coroutines

import org.jetbrains.kotlin.builtins.isFunctionOrSuspendFunctionType
import org.jetbrains.kotlin.builtins.isSuspendFunctionType
import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ValueParameterDescriptor
import org.jetbrains.kotlin.descriptors.impl.AnonymousFunctionDescriptor

val CallableDescriptor.isSuspendLambda get() = this is AnonymousFunctionDescriptor && this.isSuspend

val ValueParameterDescriptor.hasSuspendFunctionType get() = returnType?.isSuspendFunctionType == true

val ValueParameterDescriptor.hasFunctionOrSuspendFunctionType get() = returnType?.isFunctionOrSuspendFunctionType == true
