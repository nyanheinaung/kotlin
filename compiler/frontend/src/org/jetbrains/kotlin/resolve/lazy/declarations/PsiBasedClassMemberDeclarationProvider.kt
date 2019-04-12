/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import org.jetbrains.kotlin.resolve.lazy.data.KtClassLikeInfo
import org.jetbrains.kotlin.storage.StorageManager

class PsiBasedClassMemberDeclarationProvider(
    storageManager: StorageManager,
    override val ownerInfo: KtClassLikeInfo
) : AbstractPsiBasedDeclarationProvider(storageManager), ClassMemberDeclarationProvider {

    override fun doCreateIndex(index: AbstractPsiBasedDeclarationProvider.Index) {
        for (declaration in ownerInfo.declarations) {
            index.putToIndex(declaration)
        }

        for (parameter in ownerInfo.primaryConstructorParameters) {
            if (parameter.hasValOrVar()) {
                index.putToIndex(parameter)
            }
        }
    }

    override fun toString() = "Declarations for $ownerInfo"
}
