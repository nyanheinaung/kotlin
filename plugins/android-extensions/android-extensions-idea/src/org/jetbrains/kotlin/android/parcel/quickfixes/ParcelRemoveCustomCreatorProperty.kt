/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.parcel.quickfixes

import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory

class ParcelRemoveCustomCreatorProperty(property: KtProperty) : AbstractParcelableQuickFix<KtProperty>(property) {
    object Factory : AbstractFactory(f@ {
        // KtProperty or its name identifier
        psiElement as? KtProperty ?: (psiElement.parent as? KtProperty) ?: return@f null
        findElement<KtProperty>()?.let(::ParcelRemoveCustomCreatorProperty)
    })

    override fun getText() = "Remove custom ''CREATOR'' property"

    override fun invoke(ktPsiFactory: KtPsiFactory, element: KtProperty) {
        element.delete()
    }
}