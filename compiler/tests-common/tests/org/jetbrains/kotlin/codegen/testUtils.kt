/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import junit.framework.TestCase
import org.jetbrains.kotlin.load.java.JvmAbi
import org.jetbrains.kotlin.utils.rethrow
import java.lang.reflect.Method
import java.net.URL
import java.net.URLClassLoader

fun clearReflectionCache(classLoader: ClassLoader) {
    try {
        val klass = classLoader.loadClass(JvmAbi.REFLECTION_FACTORY_IMPL.asSingleFqName().asString())
        val method = klass.getDeclaredMethod("clearCaches")
        method.invoke(null)
    }
    catch (e: ClassNotFoundException) {
        // This is OK for a test without kotlin-reflect in the dependencies
    }
}



fun ClassLoader?.extractUrls(): List<URL> {
    return (this as? URLClassLoader)?.let {
        it.urLs.toList() + it.parent.extractUrls()
    } ?: emptyList()

}