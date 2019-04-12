/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import com.google.common.collect.Sets
import com.intellij.util.containers.MultiMap
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.fileClasses.JvmFileClassUtil
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.progress.ProgressIndicatorAndCompilationCanceledStatus
import org.jetbrains.kotlin.psi.KtFile

interface CodegenFactory {
    fun generateModule(state: GenerationState, files: Collection<KtFile?>, errorHandler: CompilationErrorHandler)

    fun createPackageCodegen(state: GenerationState, files: Collection<KtFile>, fqName: FqName): PackageCodegen

    fun createMultifileClassCodegen(state: GenerationState, files: Collection<KtFile>, fqName: FqName): MultifileClassCodegen

    companion object {
        fun doCheckCancelled(state: GenerationState) {
            if (state.classBuilderMode.generateBodies) {
                ProgressIndicatorAndCompilationCanceledStatus.checkCanceled()
            }
        }
    }
}

object DefaultCodegenFactory : CodegenFactory {
    override fun generateModule(state: GenerationState, files: Collection<KtFile?>, errorHandler: CompilationErrorHandler) {
        val filesInPackages = MultiMap<FqName, KtFile>()
        val filesInMultifileClasses = MultiMap<FqName, KtFile>()

        for (file in files) {
            if (file == null) throw IllegalArgumentException("A null file given for compilation")

            val fileClassInfo = JvmFileClassUtil.getFileClassInfoNoResolve(file)

            if (fileClassInfo.withJvmMultifileClass) {
                filesInMultifileClasses.putValue(fileClassInfo.facadeClassFqName, file)
            }
            else {
                filesInPackages.putValue(file.packageFqName, file)
            }
        }

        val obsoleteMultifileClasses = HashSet(state.obsoleteMultifileClasses)
        for (multifileClassFqName in filesInMultifileClasses.keySet() + obsoleteMultifileClasses) {
            CodegenFactory.doCheckCancelled(state)
            generateMultifileClass(state, multifileClassFqName, filesInMultifileClasses.get(multifileClassFqName), errorHandler)
        }

        val packagesWithObsoleteParts = HashSet(state.packagesWithObsoleteParts)
        for (packageFqName in packagesWithObsoleteParts + filesInPackages.keySet()) {
            CodegenFactory.doCheckCancelled(state)
            generatePackage(state, packageFqName, filesInPackages.get(packageFqName), errorHandler)
        }
    }

    override fun createPackageCodegen(state: GenerationState, files: Collection<KtFile>, fqName: FqName) =
            PackageCodegenImpl(state, files, fqName)

    override fun createMultifileClassCodegen(state: GenerationState, files: Collection<KtFile>, fqName: FqName) =
            MultifileClassCodegenImpl(state, files, fqName)

    private fun generateMultifileClass(
            state: GenerationState,
            multifileClassFqName: FqName,
            files: Collection<KtFile>,
            handler: CompilationErrorHandler
    ) {
        val codegen = state.factory.forMultifileClass(multifileClassFqName, files)
        codegen.generate(handler)
    }

    fun generatePackage(
            state: GenerationState,
            packageFqName: FqName,
            jetFiles: Collection<KtFile>,
            errorHandler: CompilationErrorHandler
    ) {
        // We do not really generate package class, but use old package fqName to identify package in module-info.
        //FqName packageClassFqName = PackageClassUtils.getPackageClassFqName(packageFqName);
        val codegen = state.factory.forPackage(packageFqName, jetFiles)
        codegen.generate(errorHandler)
    }
}
