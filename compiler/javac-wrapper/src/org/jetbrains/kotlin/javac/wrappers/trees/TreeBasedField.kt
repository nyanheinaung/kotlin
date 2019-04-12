/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.trees

import com.sun.source.tree.CompilationUnitTree
import com.sun.tools.javac.code.Flags
import com.sun.tools.javac.tree.JCTree
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.javac.resolve.ConstantEvaluator
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.load.java.structure.JavaType
import org.jetbrains.kotlin.name.Name

class TreeBasedField(
        tree: JCTree.JCVariableDecl,
        compilationUnit: CompilationUnitTree,
        containingClass: JavaClass,
        javac: JavacWrapper
) : TreeBasedMember<JCTree.JCVariableDecl>(tree, compilationUnit, containingClass, javac), JavaField {

    override val name: Name
        get() = Name.identifier(tree.name.toString())

    override val isAbstract: Boolean
        get() = tree.modifiers.isAbstract

    override val isStatic: Boolean
        get() = containingClass.isInterface || tree.modifiers.isStatic

    override val isFinal: Boolean
        get() = containingClass.isInterface || tree.modifiers.isFinal

    override val visibility: Visibility
        get() = if (containingClass.isInterface) Visibilities.PUBLIC else tree.modifiers.visibility

    override val isEnumEntry: Boolean
        get() = tree.modifiers.flags and Flags.ENUM.toLong() != 0L

    override val type: JavaType
        get() = TreeBasedType.create(tree.getType(), compilationUnit, javac, annotations, containingClass)

    override val initializerValue: Any?
        get() = tree.init?.let { initExpr ->
            if (hasConstantNotNullInitializer) ConstantEvaluator(containingClass, javac, compilationUnit).getValue(initExpr) else null
        }

    override val hasConstantNotNullInitializer: Boolean
        get() = tree.init?.let {
            if (it is JCTree.JCLiteral && it.value == null) return false
            val type = this.type

            isFinal && ((type is TreeBasedPrimitiveType) ||
                        (type is TreeBasedNonGenericClassifierType &&
                         type.classifierQualifiedName == "java.lang.String"))
        } ?: false

}