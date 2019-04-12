/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.parcel.quickfixes

import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.createPrimaryConstructorIfAbsent

class ParcelableAddPrimaryConstructorQuickfix(clazz: KtClass) : AbstractParcelableQuickFix<KtClass>(clazz) {
    object Factory : AbstractFactory({ findElement<KtClass>()?.let(::ParcelableAddPrimaryConstructorQuickfix) })
    override fun getText() = "Add empty primary constructor"

    override fun invoke(ktPsiFactory: KtPsiFactory, element: KtClass) {
        element.createPrimaryConstructorIfAbsent()

        for (secondaryConstructor in element.secondaryConstructors) {
            if (secondaryConstructor.getDelegationCall().isImplicit) {
                val parameterList = secondaryConstructor.valueParameterList ?: return
                val colon = secondaryConstructor.addAfter(ktPsiFactory.createColon(), parameterList)
                secondaryConstructor.addAfter(ktPsiFactory.createExpression("this()"), colon)
            }
        }
    }
}