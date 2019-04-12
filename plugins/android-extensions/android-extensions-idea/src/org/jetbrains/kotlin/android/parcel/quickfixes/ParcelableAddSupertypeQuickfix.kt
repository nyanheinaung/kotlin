/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.android.parcel.quickfixes

import org.jetbrains.kotlin.android.parcel.ANDROID_PARCELABLE_CLASS_FQNAME
import org.jetbrains.kotlin.psi.*

class ParcelableAddSupertypeQuickfix(clazz: KtClassOrObject) : AbstractParcelableQuickFix<KtClassOrObject>(clazz) {
    object Factory : AbstractFactory({ findElement<KtClassOrObject>()?.let(::ParcelableAddSupertypeQuickfix) })
    override fun getText() = "Add ''Parcelable'' supertype"

    override fun invoke(ktPsiFactory: KtPsiFactory, element: KtClassOrObject) {
        val supertypeName = ANDROID_PARCELABLE_CLASS_FQNAME.asString()
        element.addSuperTypeListEntry(ktPsiFactory.createSuperTypeEntry(supertypeName)).shortenReferences()
    }
}