/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.structure

import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaPackage
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ReflectJavaPackage(override val fqName: FqName) : ReflectJavaElement(), JavaPackage {
    override fun getClasses(nameFilter: (Name) -> Boolean): Collection<JavaClass> {
        // A package at runtime can't know what classes it has and has not
        return listOf()
    }

    override val subPackages: Collection<JavaPackage>
        get() {
            // A package at runtime can't know what sub packages it has and has not
            return listOf()
        }

    // TODO: support it if possible
    override val annotations get() = emptyList<JavaAnnotation>()

    override fun findAnnotation(fqName: FqName): JavaAnnotation? = null

    override val isDeprecatedInJavaDoc: Boolean
        get() = false

    override fun equals(other: Any?) = other is ReflectJavaPackage && fqName == other.fqName

    override fun hashCode() = fqName.hashCode()

    override fun toString() = this::class.java.name + ": " + fqName
}
