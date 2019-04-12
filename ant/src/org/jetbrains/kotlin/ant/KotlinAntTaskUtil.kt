/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ant

import org.apache.tools.ant.AntClassLoader
import org.apache.tools.ant.Task
import org.jetbrains.kotlin.preloading.ClassPreloadingUtils
import org.jetbrains.kotlin.preloading.Preloader
import java.io.File
import java.lang.ref.SoftReference
import java.net.JarURLConnection

internal object KotlinAntTaskUtil {
    private var classLoaderRef = SoftReference<ClassLoader?>(null)

    private val libPath: File by lazy {
        // Find path of kotlin-ant.jar in the filesystem and find kotlin-compiler.jar in the same directory
        val resourcePath = "/" + this::class.java.name.replace('.', '/') + ".class"
        val jarConnection = this::class.java.getResource(resourcePath).openConnection() as? JarURLConnection
                            ?: throw UnsupportedOperationException("Kotlin compiler Ant task should be loaded from the JAR file")
        val antTaskJarPath = File(jarConnection.jarFileURL.toURI())

        antTaskJarPath.parentFile
    }

    val compilerJar: File by jar("kotlin-compiler.jar")
    val runtimeJar: File by jar("kotlin-stdlib.jar")
    val reflectJar: File by jar("kotlin-reflect.jar")

    private fun jar(name: String) = lazy {
        File(libPath, name).apply {
            if (!exists()) {
                throw IllegalStateException("File is not found in the directory of Kotlin Ant task: $name")
            }
        }
    }

    @Synchronized
    fun getOrCreateClassLoader(): ClassLoader {
        val cached = classLoaderRef.get()
        if (cached != null) return cached

        val myLoader = this::class.java.classLoader
        if (myLoader !is AntClassLoader) return myLoader

        val classLoader = ClassPreloadingUtils.preloadClasses(listOf(compilerJar), Preloader.DEFAULT_CLASS_NUMBER_ESTIMATE, myLoader, null)
        classLoaderRef = SoftReference(classLoader)

        return classLoader
    }
}

internal val Task.defaultModuleName: String?
    get() = owningTarget?.name ?: project?.name
