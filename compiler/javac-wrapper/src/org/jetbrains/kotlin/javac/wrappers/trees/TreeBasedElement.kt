/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.trees

import com.sun.source.tree.CompilationUnitTree
import com.sun.tools.javac.tree.JCTree
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.JavaElement

abstract class TreeBasedElement<out T : JCTree>(
        val tree: T,
        val compilationUnit: CompilationUnitTree,
        val javac: JavacWrapper
) : JavaElement {

    override fun equals(other: Any?) = (other as? TreeBasedElement<*>)?.tree == tree

    override fun hashCode() = tree.hashCode()

    override fun toString() = tree.toString()

}
