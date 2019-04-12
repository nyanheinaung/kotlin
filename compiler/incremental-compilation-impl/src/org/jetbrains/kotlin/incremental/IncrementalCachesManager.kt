/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.incremental.storage.BasicMapsOwner
import java.io.File

abstract class IncrementalCachesManager<PlatformCache : AbstractIncrementalCache<*>>(
    cachesRootDir: File,
    protected val reporter: ICReporter
) {
    private val caches = arrayListOf<BasicMapsOwner>()
    protected fun <T : BasicMapsOwner> T.registerCache() {
        caches.add(this)
    }

    private val inputSnapshotsCacheDir = File(cachesRootDir, "inputs").apply { mkdirs() }
    private val lookupCacheDir = File(cachesRootDir, "lookups").apply { mkdirs() }

    val inputsCache: InputsCache = InputsCache(inputSnapshotsCacheDir, reporter).apply { registerCache() }
    val lookupCache: LookupStorage = LookupStorage(lookupCacheDir).apply { registerCache() }
    abstract val platformCache: PlatformCache

    fun close(flush: Boolean = false): Boolean {
        var successful = true

        for (cache in caches) {
            if (flush) {
                try {
                    cache.flush(false)
                } catch (e: Throwable) {
                    successful = false
                    reporter.report { "Exception when flushing cache ${cache.javaClass}: $e" }
                }
            }

            try {
                cache.close()
            } catch (e: Throwable) {
                successful = false
                reporter.report { "Exception when closing cache ${cache.javaClass}: $e" }
            }
        }

        return successful
    }
}

class IncrementalJvmCachesManager(
    cacheDirectory: File,
    outputDir: File,
    reporter: ICReporter
) : IncrementalCachesManager<IncrementalJvmCache>(cacheDirectory, reporter) {

    private val jvmCacheDir = File(cacheDirectory, "jvm").apply { mkdirs() }
    override val platformCache = IncrementalJvmCache(jvmCacheDir, outputDir).apply { registerCache() }
}

class IncrementalJsCachesManager(
        cachesRootDir: File,
        reporter: ICReporter
) : IncrementalCachesManager<IncrementalJsCache>(cachesRootDir, reporter) {

    private val jsCacheFile = File(cachesRootDir, "js").apply { mkdirs() }
    override val platformCache = IncrementalJsCache(jsCacheFile).apply { registerCache() }
}