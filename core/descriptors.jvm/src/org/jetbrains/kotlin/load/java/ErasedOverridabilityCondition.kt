/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.load.java.descriptors.JavaMethodDescriptor
import org.jetbrains.kotlin.load.java.lazy.types.RawSubstitution
import org.jetbrains.kotlin.load.java.lazy.types.RawTypeImpl
import org.jetbrains.kotlin.resolve.ExternalOverridabilityCondition
import org.jetbrains.kotlin.resolve.ExternalOverridabilityCondition.Result
import org.jetbrains.kotlin.resolve.OverridingUtil

class ErasedOverridabilityCondition : ExternalOverridabilityCondition {
    override fun isOverridable(
        superDescriptor: CallableDescriptor,
        subDescriptor: CallableDescriptor,
        subClassDescriptor: ClassDescriptor?
    ): Result {
        if (subDescriptor !is JavaMethodDescriptor || subDescriptor.typeParameters.isNotEmpty()) return Result.UNKNOWN

        val basicOverridability = OverridingUtil.getBasicOverridabilityProblem(superDescriptor, subDescriptor)?.result
        if (basicOverridability != null) return Result.UNKNOWN

        val signatureTypes = subDescriptor.valueParameters.asSequence().map { it.type } +
                subDescriptor.returnType!! +
                listOfNotNull(subDescriptor.extensionReceiverParameter?.type)

        if (signatureTypes.any { it.arguments.isNotEmpty() && it.unwrap() !is RawTypeImpl }) return Result.UNKNOWN

        var erasedSuper = superDescriptor.substitute(RawSubstitution.buildSubstitutor()) ?: return Result.UNKNOWN

        if (erasedSuper is SimpleFunctionDescriptor && erasedSuper.typeParameters.isNotEmpty()) {
            // Only simple functions are supported now for erased overrides
            erasedSuper = erasedSuper.newCopyBuilder().setTypeParameters(emptyList()).build()!!
        }

        val overridabilityResult =
            OverridingUtil.DEFAULT.isOverridableByWithoutExternalConditions(erasedSuper, subDescriptor, false).result
        return when (overridabilityResult) {
            OverridingUtil.OverrideCompatibilityInfo.Result.OVERRIDABLE -> Result.OVERRIDABLE
            else -> Result.UNKNOWN
        }
    }

    override fun getContract() = ExternalOverridabilityCondition.Contract.SUCCESS_ONLY
}
