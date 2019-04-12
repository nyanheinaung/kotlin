/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt3.base.util

import com.sun.tools.javac.main.Option
import com.sun.tools.javac.tree.JCTree
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Options
import com.sun.tools.javac.util.List as JavacList
import org.jetbrains.kotlin.kapt3.base.plus

fun isJava9OrLater(): Boolean = !System.getProperty("java.version").startsWith("1.")

fun Options.putJavacOption(jdk8Name: String, jdk9Name: String, value: String) {
    val option = if (isJava9OrLater()) {
        Option.valueOf(jdk9Name)
    } else {
        Option.valueOf(jdk8Name)
    }

    put(option, value)
}

@Suppress("FunctionName")
fun TreeMaker.TopLevelJava9Aware(packageClause: JCTree.JCExpression?, declarations: JavacList<JCTree>): JCTree.JCCompilationUnit {
    @Suppress("SpellCheckingInspection")
    return if (isJava9OrLater()) {
        val topLevelMethod = TreeMaker::class.java.declaredMethods.single { it.name == "TopLevel" }
        val packageDecl: JCTree? = packageClause?.let {
            val packageDeclMethod = TreeMaker::class.java.methods.single { it.name == "PackageDecl" }
            packageDeclMethod.invoke(this, JavacList.nil<JCTree>(), packageClause) as JCTree
        }
        val allDeclarations = if (packageDecl != null) JavacList.of(packageDecl) + declarations else declarations
        topLevelMethod.invoke(this, allDeclarations) as JCTree.JCCompilationUnit
    } else {
        TopLevel(JavacList.nil(), packageClause, declarations)
    }
}

fun JCTree.JCCompilationUnit.getPackageNameJava9Aware(): JCTree? {
    return if (isJava9OrLater()) {
        JCTree.JCCompilationUnit::class.java.getDeclaredMethod("getPackageName").invoke(this) as JCTree?
    } else {
        this.packageName
    }
}