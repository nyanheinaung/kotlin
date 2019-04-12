/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import org.jetbrains.kotlin.builtins.jvm.JvmBuiltIns
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.NotFoundClasses
import org.jetbrains.kotlin.descriptors.deserialization.AdditionalClassPartsProvider
import org.jetbrains.kotlin.descriptors.deserialization.PlatformDependentDeclarationFilter
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.load.java.lazy.LazyJavaPackageFragmentProvider
import org.jetbrains.kotlin.metadata.jvm.deserialization.JvmProtoBufUtil
import org.jetbrains.kotlin.serialization.deserialization.*
import org.jetbrains.kotlin.storage.StorageManager

// This class is needed only for easier injection: exact types of needed components are specified in the constructor here.
// Otherwise injector generator is not smart enough to deduce, for example, which package fragment provider DeserializationComponents needs
class DeserializationComponentsForJava(
    storageManager: StorageManager,
    moduleDescriptor: ModuleDescriptor,
    configuration: DeserializationConfiguration,
    classDataFinder: JavaClassDataFinder,
    annotationAndConstantLoader: BinaryClassAnnotationAndConstantLoaderImpl,
    packageFragmentProvider: LazyJavaPackageFragmentProvider,
    notFoundClasses: NotFoundClasses,
    errorReporter: ErrorReporter,
    lookupTracker: LookupTracker,
    contractDeserializer: ContractDeserializer
) {
    val components: DeserializationComponents

    init {
        // currently built-ins may be not an instance of JvmBuiltIns only in case of built-ins serialization
        val jvmBuiltIns = moduleDescriptor.builtIns as? JvmBuiltIns
        components = DeserializationComponents(
            storageManager, moduleDescriptor, configuration, classDataFinder, annotationAndConstantLoader, packageFragmentProvider,
            LocalClassifierTypeSettings.Default, errorReporter, lookupTracker, JavaFlexibleTypeDeserializer,
            emptyList(), notFoundClasses, contractDeserializer,
            additionalClassPartsProvider = jvmBuiltIns?.settings ?: AdditionalClassPartsProvider.None,
            platformDependentDeclarationFilter = jvmBuiltIns?.settings ?: PlatformDependentDeclarationFilter.NoPlatformDependent,
            extensionRegistryLite = JvmProtoBufUtil.EXTENSION_REGISTRY
        )
    }
}
