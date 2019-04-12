/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.context

import org.jetbrains.kotlin.descriptors.CallableDescriptor
import org.jetbrains.kotlin.psi.Call
import org.jetbrains.kotlin.resolve.DelegatingBindingTrace
import org.jetbrains.kotlin.resolve.calls.context.ResolutionResultsCache.CachedData
import org.jetbrains.kotlin.resolve.calls.results.OverloadResolutionResultsImpl
import org.jetbrains.kotlin.resolve.calls.tasks.TracingStrategy
import java.util.*

interface ResolutionResultsCache {
    data class CachedData(
        val resolutionResults: OverloadResolutionResultsImpl<*>,
        val deferredComputation: BasicCallResolutionContext,
        val tracing: TracingStrategy,
        val resolutionTrace: DelegatingBindingTrace
    )

    fun record(
        call: Call,
        results: OverloadResolutionResultsImpl<*>,
        deferredComputation: BasicCallResolutionContext,
        tracing: TracingStrategy,
        resolutionTrace: DelegatingBindingTrace
    )

    operator fun get(call: Call): CachedData?
}

class ResolutionResultsCacheImpl : ResolutionResultsCache {
    private val data = HashMap<Call, CachedData>()

    override fun record(
        call: Call,
        results: OverloadResolutionResultsImpl<out CallableDescriptor?>,
        deferredComputation: BasicCallResolutionContext,
        tracing: TracingStrategy,
        resolutionTrace: DelegatingBindingTrace
    ) {
        data[call] = CachedData(results, deferredComputation, tracing, resolutionTrace)
    }

    override fun get(call: Call): CachedData? = data[call]

    fun addData(cache: ResolutionResultsCacheImpl) {
        data.putAll(cache.data)
    }
}

class TemporaryResolutionResultsCache(private val parentCache: ResolutionResultsCache) : ResolutionResultsCache {
    private val innerCache = ResolutionResultsCacheImpl()

    override fun record(
        call: Call,
        results: OverloadResolutionResultsImpl<out CallableDescriptor?>,
        deferredComputation: BasicCallResolutionContext,
        tracing: TracingStrategy,
        resolutionTrace: DelegatingBindingTrace
    ) {
        innerCache.record(call, results, deferredComputation, tracing, resolutionTrace)
    }

    override fun get(call: Call): CachedData? = innerCache[call] ?: parentCache[call]

    fun commit() {
        when (parentCache) {
            is ResolutionResultsCacheImpl -> parentCache.addData(innerCache)
            is TemporaryResolutionResultsCache -> parentCache.innerCache.addData(innerCache)
        }
    }
}
