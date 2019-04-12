/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.builder

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.SystemInfo
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.compiled.ClsFileImpl
import com.intellij.psi.impl.java.stubs.PsiJavaFileStub
import com.intellij.psi.impl.java.stubs.impl.PsiJavaFileStubImpl
import com.intellij.psi.impl.source.tree.TreeElement
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.diagnostics.Diagnostics
import java.lang.StringBuilder

data class LightClassBuilderResult(val stub: PsiJavaFileStub, val bindingContext: BindingContext, val diagnostics: Diagnostics)

fun buildLightClass(
        packageFqName: FqName,
        files: Collection<KtFile>,
        generateClassFilter: GenerationState.GenerateClassFilter,
        context: LightClassConstructionContext,
        generate: (state: GenerationState, files: Collection<KtFile>) -> Unit
): LightClassBuilderResult {
    val project = files.first().project

    try {
        val classBuilderFactory = KotlinLightClassBuilderFactory(createJavaFileStub(project, packageFqName, files))
        val state = GenerationState.Builder(
                project,
                classBuilderFactory,
                context.module,
                context.bindingContext,
                files.toList(),
                context.languageVersionSettings?.let {
                    CompilerConfiguration().apply {
                        languageVersionSettings = it
                        isReadOnly = true
                    }
                } ?: CompilerConfiguration.EMPTY

        ).generateDeclaredClassFilter(generateClassFilter).wantsDiagnostics(false).build()
        state.beforeCompile()

        generate(state, files)

        val javaFileStub = classBuilderFactory.result()

        ServiceManager.getService(project, StubComputationTracker::class.java)?.onStubComputed(javaFileStub, context)
        return LightClassBuilderResult(javaFileStub, context.bindingContext, state.collectedExtraJvmDiagnostics)
    }
    catch (e: ProcessCanceledException) {
        throw e
    }
    catch (e: RuntimeException) {
        logErrorWithOSInfo(e, packageFqName, null)
        throw e
    }
}

private fun createJavaFileStub(project: Project, packageFqName: FqName, files: Collection<KtFile>): PsiJavaFileStub {
    val javaFileStub = PsiJavaFileStubImpl(packageFqName.asString(), /*compiled = */true)
    javaFileStub.psiFactory = ClsWrapperStubPsiFactory.INSTANCE

    val fakeFile = object : ClsFileImpl(files.first().viewProvider) {
        override fun getStub() = javaFileStub

        override fun getPackageName() = packageFqName.asString()

        override fun isPhysical() = false

        override fun appendMirrorText(indentLevel: Int, buffer: StringBuilder) {
            if (files.size == 1) {
                LOG.error("Mirror text should never be calculated for light classes generated from a single file")
            }
            super.appendMirrorText(indentLevel, buffer)
        }


        override fun setMirror(element: TreeElement) {
            if (files.size == 1) {
                LOG.error("Mirror element should never be set for light classes generated from a single file")
            }
            super.setMirror(element)
        }

        override fun getMirror(): PsiElement {
            if (files.size == 1) {
                LOG.error("Mirror element should never be calculated for light classes generated from a single file")
            }
            return super.getMirror()
        }

        override fun getText(): String {
            return files.singleOrNull()?.text ?: super.getText()
        }
    }

    javaFileStub.psi = fakeFile
    return javaFileStub
}

private fun logErrorWithOSInfo(cause: Throwable?, fqName: FqName, virtualFile: VirtualFile?) {
    val path = if (virtualFile == null) "<null>" else virtualFile.path
    LOG.error(
            "Could not generate LightClass for $fqName declared in $path\n" +
            "System: ${SystemInfo.OS_NAME} ${SystemInfo.OS_VERSION} Java Runtime: ${SystemInfo.JAVA_RUNTIME_VERSION}",
            cause
    )
}

private val LOG = Logger.getInstance(LightClassBuilderResult::class.java)