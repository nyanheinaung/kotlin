/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.synthetic.descriptors

import kotlinx.android.extensions.LayoutContainer
import org.jetbrains.kotlin.android.synthetic.AndroidConst
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.descriptors.findClassAcrossModuleDependencies
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.descriptorUtil.module
import org.jetbrains.kotlin.storage.StorageManager
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.SimpleType
import java.util.*

class LazySyntheticElementResolveContext(private val module: ModuleDescriptor, storageManager: StorageManager) {
    private val context = storageManager.createLazyValue {
        module.createResolveContext()
    }

    internal operator fun invoke() = context()

    private fun ModuleDescriptor.createResolveContext(): SyntheticElementResolveContext {
        fun find(fqName: String) = module.findClassAcrossModuleDependencies(ClassId.topLevel(FqName(fqName)))

        val viewDescriptor = find(AndroidConst.VIEW_FQNAME) ?: return SyntheticElementResolveContext.ERROR_CONTEXT
        val activityDescriptor = find(AndroidConst.ACTIVITY_FQNAME) ?: return SyntheticElementResolveContext.ERROR_CONTEXT
        val fragmentDescriptor = find(AndroidConst.FRAGMENT_FQNAME)
        val dialogDescriptor = find(AndroidConst.DIALOG_FQNAME) ?: return SyntheticElementResolveContext.ERROR_CONTEXT
        val supportActivityDescriptor = find(AndroidConst.ANDROIDX_SUPPORT_FRAGMENT_ACTIVITY_FQNAME) ?: find(AndroidConst.SUPPORT_FRAGMENT_ACTIVITY_FQNAME)
        val supportFragmentDescriptor = find(AndroidConst.ANDROIDX_SUPPORT_FRAGMENT_FQNAME) ?: find(AndroidConst.SUPPORT_FRAGMENT_FQNAME)
        val layoutContainerDescriptor = find(LayoutContainer::class.java.canonicalName)

        return SyntheticElementResolveContext(
                view = viewDescriptor.defaultType,
                activity = activityDescriptor.defaultType,
                fragment = fragmentDescriptor?.defaultType,
                dialog = dialogDescriptor.defaultType,
                supportActivity = supportActivityDescriptor?.defaultType,
                supportFragment = supportFragmentDescriptor?.defaultType,
                layoutContainer = layoutContainerDescriptor?.defaultType)
    }
}

internal class SyntheticElementResolveContext(
        val view: SimpleType,
        val activity: SimpleType,
        val fragment: SimpleType?,
        val dialog: SimpleType,
        val supportActivity: SimpleType?,
        val supportFragment: SimpleType?,
        val layoutContainer: SimpleType?
) {
    companion object {
        private fun errorType() = ErrorUtils.createErrorType("")
        val ERROR_CONTEXT = SyntheticElementResolveContext(errorType(), errorType(), null, errorType(), null, null, null)
    }

    private val widgetReceivers by lazy {
        val receivers = ArrayList<WidgetReceiver>(4)
        receivers += WidgetReceiver(activity, mayHaveCache = true)
        receivers += WidgetReceiver(dialog, mayHaveCache = false)
        fragment?.let { receivers += WidgetReceiver(it, mayHaveCache = true) }
        supportFragment?.let { receivers += WidgetReceiver(it, mayHaveCache = true) }
        receivers
    }

    private val experimentalWidgetReceivers by lazy {
        val receivers = widgetReceivers.toMutableList()
        layoutContainer?.let { receivers += WidgetReceiver(it, mayHaveCache = true) }
        receivers
    }

    val fragmentTypes: List<Pair<SimpleType, SimpleType>> by lazy {
        if (fragment == null) {
            emptyList<Pair<SimpleType, SimpleType>>()
        }
        else {
            val types = ArrayList<Pair<SimpleType, SimpleType>>(4)
            types += Pair(activity, fragment)
            types += Pair(fragment, fragment)
            if (supportActivity != null && supportFragment != null) {
                types += Pair(supportFragment, supportFragment)
                types += Pair(supportActivity, supportFragment)
            }
            types
        }
    }

    fun getWidgetReceivers(forView: Boolean, isExperimental: Boolean): List<WidgetReceiver> {
        if (forView) return listOf(WidgetReceiver(view, mayHaveCache = true))
        return if (isExperimental) experimentalWidgetReceivers else widgetReceivers
    }

}

class WidgetReceiver(val type: SimpleType, val mayHaveCache: Boolean)