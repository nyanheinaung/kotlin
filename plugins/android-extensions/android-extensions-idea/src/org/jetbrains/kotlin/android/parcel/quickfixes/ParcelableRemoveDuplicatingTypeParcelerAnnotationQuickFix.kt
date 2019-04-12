/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.parcel.quickfixes

import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtPsiFactory

class ParcelableRemoveDuplicatingTypeParcelerAnnotationQuickFix(anno: KtAnnotationEntry) : AbstractParcelableQuickFix<KtAnnotationEntry>(anno) {
    object Factory : AbstractFactory({ findElement<KtAnnotationEntry>()?.let(::ParcelableRemoveDuplicatingTypeParcelerAnnotationQuickFix) })

    override fun getText() = "Remove redundant ''@TypeParceler'' annotation"

    override fun invoke(ktPsiFactory: KtPsiFactory, element: KtAnnotationEntry) {
        element.delete()
    }
}