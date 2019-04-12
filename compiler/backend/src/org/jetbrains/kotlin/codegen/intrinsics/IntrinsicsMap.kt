/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.intrinsics

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.TypeParameterDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.FqNameUnsafe
import org.jetbrains.kotlin.resolve.DescriptorUtils
import java.util.*

internal class IntrinsicsMap {

    private val intrinsicsMap = HashMap<Key, IntrinsicMethod>()

    private data class Key(
        private val owner: FqNameUnsafe,
        private val receiverParameter: FqNameUnsafe?,
        private val name: String,
        private val valueParameterCount: Int
    )

    private fun valueParameterCountForKey(member: CallableMemberDescriptor): Int =
        if (member is PropertyDescriptor)
            -1
        else
            member.valueParameters.size


    /**
     * @param valueParameterCount -1 for property
     */
    fun registerIntrinsic(
        owner: FqName,
        receiverParameter: FqNameUnsafe?,
        name: String,
        valueParameterCount: Int,
        impl: IntrinsicMethod
    ) {
        intrinsicsMap[Key(owner.toUnsafe(), receiverParameter, name, valueParameterCount)] = impl
    }

    fun getIntrinsic(descriptor: CallableMemberDescriptor): IntrinsicMethod? =
        intrinsicsMap[getKey(descriptor)]?.takeIf { it.isApplicableToOverload(descriptor.original) }

    private fun getKey(descriptor: CallableMemberDescriptor): Key =
        Key(
            DescriptorUtils.getFqName(descriptor.containingDeclaration),
            getReceiverParameterFqName(descriptor),
            descriptor.name.asString(),
            valueParameterCountForKey(descriptor)
        )

    private fun getReceiverParameterFqName(descriptor: CallableMemberDescriptor): FqNameUnsafe? {
        val receiverParameter = descriptor.extensionReceiverParameter ?: return null
        val classifier = receiverParameter.type.constructor.declarationDescriptor ?: return null

        return if (classifier is TypeParameterDescriptor)
            IntrinsicMethods.RECEIVER_PARAMETER_FQ_NAME
        else
            DescriptorUtils.getFqName(classifier)
    }

}

