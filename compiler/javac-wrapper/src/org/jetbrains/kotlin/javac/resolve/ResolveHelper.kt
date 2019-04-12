/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.resolve

import com.sun.source.tree.CompilationUnitTree
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.javac.JavaClassWithClassId
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.JavaVisibilities
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal class ResolveHelper(private val javac: JavacWrapper,
                             private val compilationUnit: CompilationUnitTree) {

    fun getJavaClassFromPathSegments(javaClass: JavaClass,
                                     pathSegments: List<String>) =
            if (pathSegments.size == 1) {
                javaClass
            }
            else {
                javaClass.findInnerOrNested(pathSegments.drop(1))
            }

    fun findImport(pathSegments: List<String>): JavaClass? {
        pathSegments.forEachIndexed { index, _ ->
            if (index == pathSegments.lastIndex) return null
            val packageFqName = pathSegments.dropLast(index + 1).joinToString(separator = ".")
            findPackage(packageFqName)?.let { pack ->
                val className = pathSegments.takeLast(index + 1)
                return findJavaOrKotlinClass(ClassId(pack, Name.identifier(className.first())))?.let { javaClass ->
                    getJavaClassFromPathSegments(javaClass, className)
                }
            }
        }

        return null
    }

    fun findJavaOrKotlinClass(classId: ClassId) = javac.findClass(classId) ?: javac.getKotlinClassifier(classId)

    fun findInnerOrNested(javaClass: JavaClass, name: Name, checkedSupertypes: HashSet<JavaClass> = hashSetOf()): JavaClass? {
        javaClass.findVisibleInnerOrNestedClass(name)?.let {
            checkedSupertypes.addAll(javaClass.collectAllSupertypes())
            return it
        }

        return javaClass.supertypes
                .mapNotNull {
                    (it.classifier as? JavaClass)?.let { supertype ->
                        if (supertype !in checkedSupertypes) {
                            findInnerOrNested(supertype, name, checkedSupertypes)
                        }
                        else null
                    }
                }.singleOrNull()
    }

    fun findPackage(packageName: String): FqName? {
        val fqName = if (packageName.isNotBlank()) FqName(packageName) else FqName.ROOT
        javac.hasKotlinPackage(fqName)?.let { return it }

        return javac.findPackage(fqName)?.fqName
    }

    private fun JavaClass.findVisibleInnerOrNestedClass(name: Name) = findInnerClass(name)?.let { innerOrNestedClass ->
        when (innerOrNestedClass.visibility) {
            Visibilities.PRIVATE -> null
            JavaVisibilities.PACKAGE_VISIBILITY -> {
                val classId = (innerOrNestedClass as? JavaClassWithClassId)?.classId
                if (classId?.packageFqName?.asString() == (compilationUnit.packageName?.toString() ?: "")) innerOrNestedClass else null
            }
            else -> innerOrNestedClass
        }
    }

    private fun JavaClass.findInnerOrNested(pathSegments: List<String>): JavaClass? =
            pathSegments.fold(this) { javaClass, it -> findInnerOrNested(javaClass, Name.identifier(it)) ?: return null }


}

fun JavaClass.collectAllSupertypes(): Set<JavaClass> =
        hashSetOf(this).apply {
            supertypes.mapNotNull { it.classifier as? JavaClass }.forEach { addAll(it.collectAllSupertypes()) }
        }