/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.parcel.quickfixes

import kotlinx.android.parcel.Parcelize
import org.jetbrains.kotlin.android.synthetic.diagnostic.ErrorsAndroid
import org.jetbrains.kotlin.diagnostics.Errors
import org.jetbrains.kotlin.idea.util.addAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtPsiFactory

class AnnotateWithParcelizeQuickFix(clazz: KtClassOrObject) : AbstractParcelableQuickFix<KtClassOrObject>(clazz) {
    object Factory : AbstractFactory({
        val diagnostic = Errors.PLUGIN_ERROR.cast(this).a
        val targetClass = ErrorsAndroid.CLASS_SHOULD_BE_PARCELIZE.cast(diagnostic.diagnostic).a
        AnnotateWithParcelizeQuickFix(targetClass)
    })

    override fun getText() = "Annotate containing class with ''@Parcelize''"

    override fun invoke(ktPsiFactory: KtPsiFactory, element: KtClassOrObject) {
        element.addAnnotation(FqName(Parcelize::class.java.name))
    }
}
