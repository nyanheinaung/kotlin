/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm

import org.jetbrains.kotlin.backend.common.phaser.PhaseConfig
import org.jetbrains.kotlin.codegen.*
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi2ir.Psi2IrTranslator

class JvmIrCodegenFactory(private val phaseConfig: PhaseConfig) : CodegenFactory {

    override fun generateModule(state: GenerationState, files: Collection<KtFile?>, errorHandler: CompilationErrorHandler) {
        assert(!files.any { it == null })

        val psi2ir = Psi2IrTranslator(state.languageVersionSettings)
        val psi2irContext = psi2ir.createGeneratorContext(state.module, state.bindingContext, extensions = JvmGeneratorExtensions)
        val irModuleFragment = psi2ir.generateModuleFragment(psi2irContext, files as Collection<KtFile>)
        JvmBackendFacade.doGenerateFilesInternal(state, errorHandler, irModuleFragment, psi2irContext, phaseConfig)
    }

    override fun createPackageCodegen(state: GenerationState, files: Collection<KtFile>, fqName: FqName): PackageCodegen {
        val impl = PackageCodegenImpl(state, files, fqName)

        return object : PackageCodegen {
            override fun generate(errorHandler: CompilationErrorHandler) {
                JvmBackendFacade.doGenerateFiles(files, state, errorHandler, phaseConfig)
            }

            override fun getPackageFragment(): PackageFragmentDescriptor {
                return impl.packageFragment
            }
        }
    }

    override fun createMultifileClassCodegen(state: GenerationState, files: Collection<KtFile>, fqName: FqName): MultifileClassCodegen {
        TODO()
    }
}
