/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.trees

import com.sun.source.tree.CompilationUnitTree
import com.sun.tools.javac.tree.JCTree
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaAnnotation
import org.jetbrains.kotlin.load.java.structure.JavaClassifierType
import org.jetbrains.kotlin.load.java.structure.JavaElement
import org.jetbrains.kotlin.load.java.structure.JavaTypeParameter
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class TreeBasedTypeParameter(
        tree: JCTree.JCTypeParameter,
        compilationUnit: CompilationUnitTree,
        javac: JavacWrapper,
        private val containingElement: JavaElement
) : TreeBasedElement<JCTree.JCTypeParameter>(tree, compilationUnit, javac), JavaTypeParameter {

    override val name: Name
        get() = Name.identifier(tree.name.toString())

    override val annotations: Collection<JavaAnnotation> by lazy {
        tree.annotations().map { TreeBasedAnnotation(it, compilationUnit, javac, containingElement) }
    }

    override fun findAnnotation(fqName: FqName) =
            annotations.firstOrNull { it.classId?.asSingleFqName() == fqName }

    override val isDeprecatedInJavaDoc: Boolean
        get() = javac.isDeprecatedInJavaDoc(tree, compilationUnit)

    override val upperBounds: Collection<JavaClassifierType>
        get() = tree.bounds.mapNotNull {
            TreeBasedType.create(it, compilationUnit, javac, emptyList(), containingElement) as? JavaClassifierType
        }

    override fun equals(other: Any?): Boolean {
        if (other !is TreeBasedTypeParameter) return false
        return other.name == name && other.upperBounds == upperBounds
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        upperBounds.forEach { result = 37 * result + it.hashCode() }
        return result
    }

}