/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.contracts.description

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.serialization.deserialization.ContractProvider
import org.jetbrains.kotlin.storage.StorageManager

abstract class AbstractContractProvider : ContractProvider {
    abstract fun getContractDescription(): ContractDescription?
}

/**
 * Essentially, this is a composition of two fields: value of type 'ContractDescription' and
 * 'computation', which guarantees to initialize this field.
 *
 * Such contract providers are present only for source-based declarations, where we need additional
 * resolve (force-resolve of the body) to get ContractDescription
 */
class LazyContractProvider(private val storageManager: StorageManager, private val computation: () -> Any?) : AbstractContractProvider() {
    @Volatile
    private var isComputed: Boolean = false

    private var contractDescription: ContractDescription? = null


    override fun getContractDescription(): ContractDescription? {
        if (!isComputed) {
            storageManager.compute(computation)
            assert(isComputed) { "Computation of contract hasn't initialized contract properly" }
        }

        return contractDescription
    }

    fun setContractDescription(contractDescription: ContractDescription?) {
        this.contractDescription = contractDescription
        isComputed = true // publish
    }
}

/**
 * Such contract providers are used where we can be sure about contract presence and don't need
 * additional resolve (e.g., for deserialized declarations)
 */
class ContractProviderImpl(private val contractDescription: ContractDescription) : AbstractContractProvider() {
    override fun getContractDescription(): ContractDescription = contractDescription
}

// For storing into UserDataMap of FunctionDescriptor
object ContractProviderKey : CallableDescriptor.UserDataKey<AbstractContractProvider?>
