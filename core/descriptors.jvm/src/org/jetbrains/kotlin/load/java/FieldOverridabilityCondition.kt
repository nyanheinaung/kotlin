/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.load.java.lazy.descriptors.isJavaField
import org.jetbrains.kotlin.resolve.ExternalOverridabilityCondition
import org.jetbrains.kotlin.resolve.ExternalOverridabilityCondition.Result

class FieldOverridabilityCondition : ExternalOverridabilityCondition {
    override fun isOverridable(
        superDescriptor: CallableDescriptor,
        subDescriptor: CallableDescriptor,
        subClassDescriptor: ClassDescriptor?
    ): Result {
        if (subDescriptor !is PropertyDescriptor || superDescriptor !is PropertyDescriptor) return Result.UNKNOWN
        if (subDescriptor.name != superDescriptor.name) return Result.UNKNOWN

        if (subDescriptor.isJavaField && superDescriptor.isJavaField) return Result.OVERRIDABLE
        if (subDescriptor.isJavaField || superDescriptor.isJavaField) return Result.INCOMPATIBLE

        return Result.UNKNOWN
    }

    override fun getContract() = ExternalOverridabilityCondition.Contract.BOTH
}
