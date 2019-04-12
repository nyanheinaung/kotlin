/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.kotlin

import com.intellij.ide.highlighter.JavaClassFileType
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiJavaModule

class KotlinBinaryClassCache : Disposable {
    private class RequestCache {
        internal var virtualFile: VirtualFile? = null
        internal var modificationStamp: Long = 0
        internal var result: KotlinClassFinder.Result? = null

        fun cache(
            file: VirtualFile,
            result: KotlinClassFinder.Result?
        ): KotlinClassFinder.Result? {
            virtualFile = file
            this.result = result
            modificationStamp = file.modificationStamp

            return result
        }
    }

    private val cache = object : ThreadLocal<RequestCache>() {
        override fun initialValue(): RequestCache {
            return RequestCache()
        }
    }

    override fun dispose() {
        // This is only relevant for tests. We create a new instance of Application for each test, and so a new instance of this service is
        // also created for each test. However all tests share the same event dispatch thread, which would collect all instances of this
        // thread-local if they're not removed properly. Each instance would transitively retain VFS resulting in OutOfMemoryError
        cache.remove()
    }

    companion object {
        fun getKotlinBinaryClassOrClassFileContent(
            file: VirtualFile, fileContent: ByteArray? = null
        ): KotlinClassFinder.Result? {
            if (file.fileType !== JavaClassFileType.INSTANCE) return null

            if (file.name == PsiJavaModule.MODULE_INFO_CLS_FILE) return null

            val service = ServiceManager.getService(KotlinBinaryClassCache::class.java)
            val requestCache = service.cache.get()

            if (file.modificationStamp == requestCache.modificationStamp && file == requestCache.virtualFile) {
                return requestCache.result
            }

            val aClass = ApplicationManager.getApplication().runReadAction(Computable {
                @Suppress("DEPRECATION")
                VirtualFileKotlinClass.create(file, fileContent)
            })

            return requestCache.cache(file, aClass)
        }
    }
}
