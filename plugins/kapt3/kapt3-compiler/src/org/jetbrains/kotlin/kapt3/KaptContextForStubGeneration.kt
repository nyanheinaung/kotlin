/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt3

import com.intellij.openapi.project.Project
import com.sun.tools.javac.tree.TreeMaker
import com.sun.tools.javac.util.Context
import org.jetbrains.kotlin.base.kapt3.KaptOptions
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.kapt3.base.KaptContext
import org.jetbrains.kotlin.kapt3.base.util.KaptLogger
import org.jetbrains.kotlin.kapt3.javac.KaptTreeMaker
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.org.objectweb.asm.tree.ClassNode

class KaptContextForStubGeneration(
    options: KaptOptions,
    withJdk: Boolean,
    logger: KaptLogger,
    val project: Project,
    val bindingContext: BindingContext,
    val compiledClasses: List<ClassNode>,
    val origins: Map<Any, JvmDeclarationOrigin>,
    val generationState: GenerationState
) : KaptContext(options, withJdk, logger) {
    private val treeMaker = TreeMaker.instance(context)

    override fun preregisterTreeMaker(context: Context) {
        KaptTreeMaker.preRegister(context, this)
    }

    override fun close() {
        (treeMaker as? KaptTreeMaker)?.dispose()
        generationState.destroy()
        super.close()
    }
}