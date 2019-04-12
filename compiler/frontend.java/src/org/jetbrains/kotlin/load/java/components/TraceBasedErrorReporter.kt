/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.components

import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.OverrideResolver
import org.jetbrains.kotlin.serialization.deserialization.ErrorReporter
import org.jetbrains.kotlin.util.slicedMap.BasicWritableSlice
import org.jetbrains.kotlin.util.slicedMap.Slices
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

class TraceBasedErrorReporter(private val trace: BindingTrace) : ErrorReporter {
    companion object {
        @JvmField
        val INCOMPLETE_HIERARCHY: WritableSlice<ClassDescriptor, List<String>> = Slices.createCollectiveSlice()

        init {
            BasicWritableSlice.initSliceDebugNames(TraceBasedErrorReporter::class.java)
        }
    }

    override fun reportIncompleteHierarchy(descriptor: ClassDescriptor, unresolvedSuperClasses: List<String>) {
        trace.record(INCOMPLETE_HIERARCHY, descriptor, unresolvedSuperClasses)
    }

    override fun reportCannotInferVisibility(descriptor: CallableMemberDescriptor) {
        OverrideResolver.createCannotInferVisibilityReporter(trace).invoke(descriptor)
    }
}
