/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.MemberDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.resolve.descriptorUtil.module

sealed class MultiTargetPlatform : Comparable<MultiTargetPlatform> {
    object Common : MultiTargetPlatform() {
        override fun compareTo(other: MultiTargetPlatform): Int =
                if (other is Common) 0 else -1
    }

    data class Specific(val platform: String) : MultiTargetPlatform() {
        override fun compareTo(other: MultiTargetPlatform): Int =
                when (other) {
                    is Common -> 1
                    is Specific -> platform.compareTo(other.platform)
                }
    }

    companion object {
        @JvmField
        val CAPABILITY = ModuleDescriptor.Capability<MultiTargetPlatform>("MULTI_TARGET_PLATFORM")
    }
}

fun ModuleDescriptor.getMultiTargetPlatform(): MultiTargetPlatform? =
        module.getCapability(MultiTargetPlatform.CAPABILITY)

fun MemberDescriptor.getMultiTargetPlatform(): String? =
        (module.getMultiTargetPlatform() as? MultiTargetPlatform.Specific)?.platform
