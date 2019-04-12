/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy

import com.intellij.openapi.util.Key
import org.jetbrains.kotlin.container.DefaultImplementation
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.UserDataProperty
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.bindingContextUtil.recordScope
import org.jetbrains.kotlin.resolve.scopes.LexicalScope
import org.jetbrains.kotlin.storage.StorageManager

@DefaultImplementation(FileScopeProviderImpl::class)
interface FileScopeProvider {
    fun getFileResolutionScope(file: KtFile): LexicalScope = getFileScopes(file).lexicalScope
    fun getImportResolver(file: KtFile): ImportForceResolver = getFileScopes(file).importForceResolver

    fun getFileScopes(file: KtFile): FileScopes

    object ThrowException : FileScopeProvider {
        override fun getFileScopes(file: KtFile) = throw UnsupportedOperationException("Should not be called")
    }
}

class FileScopeProviderImpl(
    private val fileScopeFactory: FileScopeFactory,
    private val bindingTrace: BindingTrace,
    private val storageManager: StorageManager
) : FileScopeProvider {

    private val cache = storageManager.createMemoizedFunction<KtFile, FileScopes> { file ->
        val scopes = (file.originalFile as KtFile?)?.fileScopesCustomizer?.createFileScopes(fileScopeFactory)
                ?: fileScopeFactory.createScopesForFile(file)
        bindingTrace.recordScope(scopes.lexicalScope, file)
        scopes
    }

    override fun getFileScopes(file: KtFile) = cache(file)
}

interface FileScopesCustomizer {
    fun createFileScopes(fileScopeFactory: FileScopeFactory): FileScopes
}

var KtFile.fileScopesCustomizer: FileScopesCustomizer? by UserDataProperty(Key.create("FILE_SCOPES_CUSTOMIZER"))
