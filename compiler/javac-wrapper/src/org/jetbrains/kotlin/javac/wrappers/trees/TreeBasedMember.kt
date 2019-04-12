/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.trees

import com.sun.source.tree.CompilationUnitTree
import com.sun.tools.javac.tree.JCTree
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaMember
import org.jetbrains.kotlin.name.FqName

abstract class TreeBasedMember<out T : JCTree>(
        tree: T,
        compilationUnit: CompilationUnitTree,
        override val containingClass: JavaClass,
        javac: JavacWrapper
) : TreeBasedElement<T>(tree, compilationUnit, javac), JavaMember {

    override val isDeprecatedInJavaDoc: Boolean
        get() = javac.isDeprecatedInJavaDoc(tree, compilationUnit)

    override val annotations: Collection<TreeBasedAnnotation> by lazy {
        tree.annotations().map { TreeBasedAnnotation(it, compilationUnit, javac, containingClass) } }

    override fun findAnnotation(fqName: FqName) =
            annotations
                    .filter { it.annotation.annotationType.toString().endsWith(fqName.shortName().asString()) }
                    .find { it.classId?.asSingleFqName() == fqName }

}