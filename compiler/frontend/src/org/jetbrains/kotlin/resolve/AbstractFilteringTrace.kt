/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.diagnostics.Diagnostic
import org.jetbrains.kotlin.util.slicedMap.WritableSlice

/**
 * Trace which allows to keep some slices hidden from the parent trace.
 *
 * Compared with TemporaryBindingTrace + TraceEntryFilter, FilteringTrace doesn't
 * make extra moves for slices that should be definitely recorded into parent
 * (like storing them in the local map, later re-committing into parent's, etc.)
 */
abstract class AbstractFilteringTrace(
    private val parentTrace: BindingTrace,
    name: String
) : DelegatingBindingTrace(parentTrace.bindingContext, name, true, BindingTraceFilter.ACCEPT_ALL, false) {
    abstract protected fun <K, V> shouldBeHiddenFromParent(slice: WritableSlice<K, V>, key: K): Boolean

    override fun <K, V> record(slice: WritableSlice<K, V>, key: K, value: V) {
        if (shouldBeHiddenFromParent(slice, key)) super.record(slice, key, value) else parentTrace.record(slice, key, value)
    }

    override fun report(diagnostic: Diagnostic) {
        parentTrace.report(diagnostic)
    }
}