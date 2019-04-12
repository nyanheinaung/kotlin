/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.synthetic

import com.intellij.openapi.project.Project
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.load.java.components.SamConversionResolver
import org.jetbrains.kotlin.resolve.deprecation.DeprecationResolver
import org.jetbrains.kotlin.resolve.scopes.SyntheticScope
import org.jetbrains.kotlin.resolve.scopes.SyntheticScopes
import org.jetbrains.kotlin.storage.StorageManager

class JavaSyntheticScopes(
        private val project: Project,
        private val moduleDescriptor: ModuleDescriptor,
        storageManager: StorageManager,
        lookupTracker: LookupTracker,
        languageVersionSettings: LanguageVersionSettings,
        samConventionResolver: SamConversionResolver,
        deprecationResolver: DeprecationResolver
): SyntheticScopes {
    override val scopes = run {
        val javaSyntheticPropertiesScope = JavaSyntheticPropertiesScope(storageManager, lookupTracker)

        val scopesFromExtensions = SyntheticScopeProviderExtension
            .getInstances(project)
            .flatMap { it.getScopes(moduleDescriptor, javaSyntheticPropertiesScope) }

        listOf(
            javaSyntheticPropertiesScope,
            SamAdapterFunctionsScope(
                storageManager, languageVersionSettings, samConventionResolver, deprecationResolver,
                lookupTracker
            )
        ) + scopesFromExtensions
    }
}

interface SyntheticScopeProviderExtension {
    companion object : ProjectExtensionDescriptor<SyntheticScopeProviderExtension>(
        "org.jetbrains.kotlin.syntheticScopeProviderExtension", SyntheticScopeProviderExtension::class.java)

    fun getScopes(moduleDescriptor: ModuleDescriptor, javaSyntheticPropertiesScope: JavaSyntheticPropertiesScope): List<SyntheticScope>
}