/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.sam

import org.jetbrains.kotlin.load.java.components.SamConversionResolver
import org.jetbrains.kotlin.load.java.descriptors.JavaClassDescriptor
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.SimpleType

class SamConversionResolverImpl(
        storageManager: StorageManager,
        private val samWithReceiverResolvers: Iterable<SamWithReceiverResolver>
): SamConversionResolver {
    private val functionTypesForSamInterfaces = storageManager.createCacheWithNullableValues<JavaClassDescriptor, SimpleType>()

    override fun resolveFunctionTypeIfSamInterface(classDescriptor: JavaClassDescriptor): SimpleType? {
        return functionTypesForSamInterfaces.computeIfAbsent(classDescriptor) {
            val abstractMethod = SingleAbstractMethodUtils.getSingleAbstractMethodOrNull(classDescriptor) ?: return@computeIfAbsent null
            val shouldConvertFirstParameterToDescriptor = samWithReceiverResolvers.any { it.shouldConvertFirstSamParameterToReceiver(abstractMethod) }
            SingleAbstractMethodUtils.getFunctionTypeForAbstractMethod(abstractMethod, shouldConvertFirstParameterToDescriptor)
        }
    }
}
