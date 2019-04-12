/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.descriptors

import org.jetbrains.kotlin.android.synthetic.res.*
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.impl.PackageFragmentDescriptorImpl
import org.jetbrains.kotlin.incremental.ANDROID_LAYOUT_CONTENT_LOOKUP_NAME
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.incremental.recordPackageLookup
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.resolve.scopes.MemberScope
import org.jetbrains.kotlin.resolve.scopes.MemberScopeImpl
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.utils.Printer
import java.util.*

class AndroidSyntheticPackageData(
        val moduleData: AndroidModuleData,
        val forView: Boolean,
        val isDeprecated: Boolean,
        val lazyResources: () -> List<AndroidResource>)

class AndroidSyntheticPackageFragmentDescriptor(
        module: ModuleDescriptor,
        fqName: FqName,
        val packageData: AndroidSyntheticPackageData,
        private val lazyContext: LazySyntheticElementResolveContext,
        private val storageManager: StorageManager,
        private val isExperimental: Boolean,
        private val lookupTracker: LookupTracker,
        private val layoutName: String
) : PackageFragmentDescriptorImpl(module, fqName) {
    private val scope = AndroidExtensionPropertiesScope()
    override fun getMemberScope(): MemberScope = scope

    private inner class AndroidExtensionPropertiesScope : MemberScopeImpl() {
        private val properties = storageManager.createLazyValue {
            val packageFragmentDescriptor = this@AndroidSyntheticPackageFragmentDescriptor

            val context = lazyContext()
            val widgetReceivers = context.getWidgetReceivers(packageData.forView, isExperimental)
            val fragmentTypes = context.fragmentTypes

            val properties = ArrayList<PropertyDescriptor>(0)
            for (resource in packageData.lazyResources()) {
                when (resource) {
                    is AndroidResource.Widget -> {
                        val resolvedWidget = resource.resolve(module)
                        if (resolvedWidget != null) {
                            for (receiver in widgetReceivers) {
                                properties += genPropertyForWidget(packageFragmentDescriptor, receiver.type, resolvedWidget, context)
                            }
                        }
                    }
                    is AndroidResource.Fragment -> if (!packageData.forView) {
                        for ((receiverType, type) in fragmentTypes) {
                            properties += genPropertyForFragment(packageFragmentDescriptor, receiverType, type, resource)
                        }
                    }
                }
            }

            properties
        }

        override fun getContributedDescriptors(kindFilter: DescriptorKindFilter, nameFilter: (Name) -> Boolean) =
                properties().filter { kindFilter.acceptsKinds(DescriptorKindFilter.VARIABLES_MASK) && nameFilter(it.name) }

        override fun getContributedVariables(name: Name, location: LookupLocation): List<PropertyDescriptor> {
            recordLookup(name, location)
            return properties().filter { it.name == name }
        }

        override fun recordLookup(name: Name, location: LookupLocation) {
            lookupTracker.recordPackageLookup(location, layoutName, ANDROID_LAYOUT_CONTENT_LOOKUP_NAME)
        }

        override fun printScopeStructure(p: Printer) {
            p.println(this::class.java.simpleName)
        }
    }
}
