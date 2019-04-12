/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.descriptors.FunctionDescriptor
import org.jetbrains.kotlin.metadata.ProtoBuf
import org.jetbrains.kotlin.metadata.deserialization.TypeTable

interface ContractDeserializer {
    fun deserializeContractFromFunction(
        proto: ProtoBuf.Function,
        ownerFunction: FunctionDescriptor,
        typeTable: TypeTable,
        typeDeserializer: TypeDeserializer
    ): Pair<CallableDescriptor.UserDataKey<*>, ContractProvider>?

    companion object {
        val DEFAULT = object : ContractDeserializer {
            override fun deserializeContractFromFunction(
                proto: ProtoBuf.Function,
                ownerFunction: FunctionDescriptor,
                typeTable: TypeTable,
                typeDeserializer: TypeDeserializer
            ): Pair<CallableDescriptor.UserDataKey<*>, Nothing>? = null
        }
    }
}

interface ContractProvider
