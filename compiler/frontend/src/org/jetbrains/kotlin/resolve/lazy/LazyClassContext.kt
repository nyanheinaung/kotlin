/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProviderFactory
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.WrappedTypeFactory

interface LazyClassContext {
    val declarationScopeProvider: DeclarationScopeProvider

    val storageManager: StorageManager
    val trace: BindingTrace
    val moduleDescriptor: ModuleDescriptor
    val descriptorResolver: DescriptorResolver
    val functionDescriptorResolver: FunctionDescriptorResolver
    val typeResolver: TypeResolver
    val declarationProviderFactory: DeclarationProviderFactory
    val annotationResolver: AnnotationResolver
    val lookupTracker: LookupTracker
    val supertypeLoopChecker: SupertypeLoopChecker
    val languageVersionSettings: LanguageVersionSettings
    val syntheticResolveExtension: SyntheticResolveExtension
    val delegationFilter: DelegationFilter
    val wrappedTypeFactory: WrappedTypeFactory
}
