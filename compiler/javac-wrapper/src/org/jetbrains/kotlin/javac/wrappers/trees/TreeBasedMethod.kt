/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac.wrappers.trees

import com.sun.source.tree.CompilationUnitTree
import com.sun.tools.javac.tree.JCTree
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.Visibility
import org.jetbrains.kotlin.javac.JavacWrapper
import org.jetbrains.kotlin.load.java.structure.*
import org.jetbrains.kotlin.name.Name

class TreeBasedMethod(
        tree: JCTree.JCMethodDecl,
        compilationUnit: CompilationUnitTree,
        containingClass: JavaClass,
        javac: JavacWrapper
) : TreeBasedMember<JCTree.JCMethodDecl>(tree, compilationUnit, containingClass, javac), JavaMethod {

    override val name: Name
        get() = Name.identifier(tree.name.toString())

    override val isAbstract: Boolean
        get() = (containingClass.isInterface && !tree.modifiers.hasDefaultModifier && !isStatic) || tree.modifiers.isAbstract

    override val isStatic: Boolean
        get() = tree.modifiers.isStatic

    override val isFinal: Boolean
        get() = tree.modifiers.isFinal

    override val visibility: Visibility
        get() = if (containingClass.isInterface) Visibilities.PUBLIC else tree.modifiers.visibility

    override val typeParameters: List<JavaTypeParameter>
        get() = tree.typeParameters.map { TreeBasedTypeParameter(it, compilationUnit, javac, this) }

    override val valueParameters: List<JavaValueParameter>
        get() = tree.parameters.map { TreeBasedValueParameter(it, compilationUnit, javac, this) }

    override val returnType: JavaType
        get() = TreeBasedType.create(tree.returnType, compilationUnit, javac, annotations, this)

    // TODO: allow nullable names in Tree-based annotation arguments and pass null instead of a synthetic name
    override val annotationParameterDefaultValue: JavaAnnotationArgument?
        get() = tree.defaultValue?.let { defaultValue ->
            createAnnotationArgument(defaultValue, Name.identifier("value"), compilationUnit, javac, containingClass, this)
        }
}
