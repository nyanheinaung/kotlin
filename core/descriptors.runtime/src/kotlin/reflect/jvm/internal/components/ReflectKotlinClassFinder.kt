/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.components

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.kotlin.KotlinClassFinder
import org.jetbrains.kotlin.load.kotlin.KotlinClassFinder.Result.KotlinClass
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.serialization.deserialization.builtins.BuiltInSerializerProtocol
import java.io.InputStream

class ReflectKotlinClassFinder(private val classLoader: ClassLoader) : KotlinClassFinder {
    private fun findKotlinClass(fqName: String): KotlinClassFinder.Result? {
        return classLoader.tryLoadClass(fqName)?.let { ReflectKotlinClass.create(it) }?.let(::KotlinClass)
    }

    override fun findKotlinClassOrContent(classId: ClassId) = findKotlinClass(classId.toRuntimeFqName())

    override fun findKotlinClassOrContent(javaClass: JavaClass): KotlinClassFinder.Result? {
        // TODO: go through javaClass's class loader
        return findKotlinClass(javaClass.fqName?.asString() ?: return null)
    }

    // TODO
    override fun findMetadata(classId: ClassId): InputStream? = null

    // TODO
    override fun hasMetadataPackage(fqName: FqName): Boolean = false

    override fun findBuiltInsData(packageFqName: FqName): InputStream? {
        if (!packageFqName.startsWith(KotlinBuiltIns.BUILT_INS_PACKAGE_NAME)) return null

        return classLoader.getResourceAsStream(BuiltInSerializerProtocol.getBuiltInsFilePath(packageFqName))
    }
}

private fun ClassId.toRuntimeFqName(): String {
    val className = relativeClassName.asString().replace('.', '$')
    return if (packageFqName.isRoot) className else "$packageFqName.$className"
}
