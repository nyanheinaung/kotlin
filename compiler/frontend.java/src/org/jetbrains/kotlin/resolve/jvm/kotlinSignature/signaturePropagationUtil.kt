/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm.kotlinSignature

import org.jetbrains.kotlin.descriptors.FunctionDescriptor

internal fun FunctionDescriptor.containsVarargs() = valueParameters.any { it.varargElementType != null }

internal fun Collection<FunctionDescriptor>.containsAnyNotTrivialSignature() = any { it.hasNotTrivialSignature() }

private fun FunctionDescriptor.hasNotTrivialSignature(): Boolean {
    if (extensionReceiverParameter != null) return true
    if (hasStableParameterNames()) return true

    return containsVarargs()
}
