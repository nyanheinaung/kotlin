/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.parcel.quickfixes

import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtPsiFactory

class ParcelRemoveCustomWriteToParcel(function: KtFunction) : AbstractParcelableQuickFix<KtFunction>(function) {
    object Factory : AbstractFactory({ findElement<KtFunction>()?.let(::ParcelRemoveCustomWriteToParcel) })
    override fun getText() = "Remove custom ''writeToParcel()'' function"

    override fun invoke(ktPsiFactory: KtPsiFactory, element: KtFunction) {
        element.delete()
    }
}