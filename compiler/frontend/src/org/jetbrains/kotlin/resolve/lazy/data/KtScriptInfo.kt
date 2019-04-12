/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.lazy.data

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtObjectDeclaration
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtScript

class KtScriptInfo(
    val script: KtScript
) : KtClassLikeInfo {
    override fun getContainingPackageFqName() = script.fqName.parent()
    override fun getModifierList() = null
    override fun getCompanionObjects() = listOf<KtObjectDeclaration>()
    override fun getScopeAnchor() = script
    override fun getCorrespondingClassOrObject() = null
    override fun getTypeParameterList() = null
    override fun getPrimaryConstructorParameters() = listOf<KtParameter>()
    override fun getClassKind() = ClassKind.CLASS
    override fun getDeclarations() = script.declarations
    override fun getDanglingAnnotations() = listOf<KtAnnotationEntry>()
}