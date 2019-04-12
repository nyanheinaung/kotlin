/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.parcel.quickfixes

import kotlinx.android.parcel.IgnoredOnParcel
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiFactory

class ParcelableAddIgnoreOnParcelAnnotationQuickfix(property: KtProperty) : AbstractParcelableQuickFix<KtProperty>(property) {
    object Factory : AbstractFactory({ findElement<KtProperty>()?.let(::ParcelableAddIgnoreOnParcelAnnotationQuickfix) })
    override fun getText() = "Add ''@IgnoredOnParcel'' annotation"

    override fun invoke(ktPsiFactory: KtPsiFactory, element: KtProperty) {
        element.addAnnotationEntry(ktPsiFactory.createAnnotationEntry("@" + IgnoredOnParcel::class.java.name)).shortenReferences()
    }
}