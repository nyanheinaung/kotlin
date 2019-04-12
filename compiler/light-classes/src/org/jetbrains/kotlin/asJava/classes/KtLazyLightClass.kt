/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.asJava.classes

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiManager
import org.jetbrains.kotlin.asJava.builder.LightClassData

abstract class KtLazyLightClass(manager: PsiManager) : KtLightClassBase(manager) {
    abstract val lightClassData: LightClassData

    override val clsDelegate: PsiClass by lazyPub { lightClassData.clsDelegate }

    override fun getOwnFields() = lightClassData.getOwnFields(this)
    override fun getOwnMethods() = lightClassData.getOwnMethods(this)
}