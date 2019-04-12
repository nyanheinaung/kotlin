/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.components

import org.jetbrains.kotlin.load.java.JavaClassFinder
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaPackage
import org.jetbrains.kotlin.name.FqName
import kotlin.reflect.jvm.internal.structure.ReflectJavaClass
import kotlin.reflect.jvm.internal.structure.ReflectJavaPackage

class ReflectJavaClassFinder(private val classLoader: ClassLoader) : JavaClassFinder {
    override fun findClass(request: JavaClassFinder.Request): JavaClass? {
        val classId = request.classId
        val packageFqName = classId.packageFqName
        val relativeClassName = classId.relativeClassName.asString().replace('.', '$')
        val name =
            if (packageFqName.isRoot) relativeClassName
            else packageFqName.asString() + "." + relativeClassName

        val klass = classLoader.tryLoadClass(name)
        return if (klass != null) ReflectJavaClass(klass) else null
    }

    override fun findPackage(fqName: FqName): JavaPackage? {
        // We don't know which packages our class loader has and has not, so we behave as if it contains any package in the world
        return ReflectJavaPackage(fqName)
    }

    override fun knownClassNamesInPackage(packageFqName: FqName): Set<String>? = null
}

fun ClassLoader.tryLoadClass(fqName: String) =
    try {
        loadClass(fqName)
    } catch (e: ClassNotFoundException) {
        null
    }
