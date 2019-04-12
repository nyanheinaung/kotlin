/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.declarations

import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtPureClassOrObject
import org.jetbrains.kotlin.resolve.lazy.data.KtClassLikeInfo

interface ClassMemberDeclarationProvider : DeclarationProvider {
    val ownerInfo: KtClassLikeInfo? // is null for synthetic classes/object that don't present in the source code

    val correspondingClassOrObject: KtPureClassOrObject? get() = ownerInfo?.correspondingClassOrObject
    val primaryConstructorParameters: List<KtParameter> get() = ownerInfo?.primaryConstructorParameters ?: emptyList()
    val companionObjects: List<KtObjectDeclaration> get() = ownerInfo?.companionObjects ?: emptyList()
}
