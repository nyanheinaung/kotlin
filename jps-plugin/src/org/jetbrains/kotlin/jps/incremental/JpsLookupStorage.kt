/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.jps.incremental

import com.intellij.openapi.diagnostic.Logger
import org.jetbrains.jps.builders.storage.BuildDataCorruptedException
import org.jetbrains.jps.builders.storage.StorageProvider
import org.jetbrains.jps.incremental.storage.BuildDataManager
import org.jetbrains.jps.incremental.storage.StorageOwner
import org.jetbrains.kotlin.incremental.LookupStorage
import java.io.File
import java.io.IOException

private object LookupStorageLock

fun BuildDataManager.cleanLookupStorage(log: Logger) {
    synchronized(LookupStorageLock) {
        try {
            cleanTargetStorages(KotlinDataContainerTarget)
        } catch (e: IOException) {
            if (!dataPaths.getTargetDataRoot(KotlinDataContainerTarget).deleteRecursively()) {
                log.debug("Could not clear lookup storage caches", e)
            }
        }
    }
}

fun <T> BuildDataManager.withLookupStorage(fn: (LookupStorage) -> T): T {
    synchronized(LookupStorageLock) {
        try {
            val lookupStorage = getStorage(KotlinDataContainerTarget, JpsLookupStorageProvider)
            return fn(lookupStorage)
        } catch (e: IOException) {
            throw BuildDataCorruptedException(e)
        }
    }
}

private object JpsLookupStorageProvider : StorageProvider<JpsLookupStorage>() {
    override fun createStorage(targetDataDir: File): JpsLookupStorage = JpsLookupStorage(targetDataDir)
}

private class JpsLookupStorage(targetDataDir: File) : StorageOwner, LookupStorage(targetDataDir)
