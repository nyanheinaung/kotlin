/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.search.declarationsSearch

import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiModifier
import com.intellij.psi.search.searches.ClassInheritorsSearch
import com.intellij.util.EmptyQuery
import com.intellij.util.Query
import org.jetbrains.kotlin.asJava.toLightClassWithBuiltinMapping
import org.jetbrains.kotlin.idea.caches.lightClasses.KtFakeLightClass
import org.jetbrains.kotlin.idea.util.application.runReadAction
import org.jetbrains.kotlin.psi.KtClassOrObject

fun HierarchySearchRequest<*>.searchInheritors(): Query<PsiClass> {
    val psiClass: PsiClass = when (originalElement) {
        is KtClassOrObject -> runReadAction { originalElement.toLightClassWithBuiltinMapping() ?: KtFakeLightClass(originalElement) }
        is PsiClass -> originalElement
        else -> null
    } ?: return EmptyQuery.getEmptyQuery()

    return ClassInheritorsSearch.search(
        psiClass,
        searchScope,
        searchDeeply,
        /* checkInheritance = */ true,
        /* includeAnonymous = */ true
    )
}

fun PsiClass.isInheritable(): Boolean = !(this is PsiAnonymousClass || hasModifierProperty(PsiModifier.FINAL))
