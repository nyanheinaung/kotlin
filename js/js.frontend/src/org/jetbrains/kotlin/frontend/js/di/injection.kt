/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.frontend.js.di

import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.config.TargetPlatformVersion
import org.jetbrains.kotlin.container.get
import org.jetbrains.kotlin.container.useImpl
import org.jetbrains.kotlin.container.useInstance
import org.jetbrains.kotlin.context.ModuleContext
import org.jetbrains.kotlin.descriptors.PackageFragmentProvider
import org.jetbrains.kotlin.descriptors.impl.CompositePackageFragmentProvider
import org.jetbrains.kotlin.descriptors.impl.ModuleDescriptorImpl
import org.jetbrains.kotlin.frontend.di.configureModule
import org.jetbrains.kotlin.incremental.components.ExpectActualTracker
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.js.resolve.JsPlatform
import org.jetbrains.kotlin.resolve.*
import org.jetbrains.kotlin.resolve.lazy.KotlinCodeAnalyzer
import org.jetbrains.kotlin.resolve.lazy.ResolveSession
import org.jetbrains.kotlin.resolve.lazy.declarations.DeclarationProviderFactory
import org.jetbrains.kotlin.serialization.deserialization.DeserializationConfiguration
import org.jetbrains.kotlin.serialization.js.KotlinJavascriptSerializationUtil
import org.jetbrains.kotlin.serialization.js.PackagesWithHeaderMetadata

fun createTopDownAnalyzerForJs(
        moduleContext: ModuleContext,
        bindingTrace: BindingTrace,
        declarationProviderFactory: DeclarationProviderFactory,
        languageVersionSettings: LanguageVersionSettings,
        lookupTracker: LookupTracker,
        expectActualTracker: ExpectActualTracker,
        additionalPackages: List<PackageFragmentProvider>
): LazyTopDownAnalyzer {
    val storageComponentContainer = createContainer("TopDownAnalyzerForJs", JsPlatform) {
        configureModule(moduleContext, JsPlatform, TargetPlatformVersion.NoVersion, bindingTrace)

        useInstance(declarationProviderFactory)
        useImpl<AnnotationResolverImpl>()

        CompilerEnvironment.configure(this)
        useInstance(lookupTracker)
        useInstance(expectActualTracker)

        useInstance(languageVersionSettings)
        useImpl<ResolveSession>()
        useImpl<LazyTopDownAnalyzer>()
    }.apply {
        val packagePartProviders = mutableListOf(get<KotlinCodeAnalyzer>().packageFragmentProvider)
        val moduleDescriptor = get<ModuleDescriptorImpl>()
        packagePartProviders += additionalPackages
        moduleDescriptor.initialize(CompositePackageFragmentProvider(packagePartProviders))
    }
    return storageComponentContainer.get<LazyTopDownAnalyzer>()
}
