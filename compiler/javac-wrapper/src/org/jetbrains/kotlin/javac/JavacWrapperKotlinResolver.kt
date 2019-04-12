/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.javac

import org.jetbrains.kotlin.load.java.structure.JavaField
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile

interface JavacWrapperKotlinResolver {
    fun resolveSupertypes(classOrObject: KtClassOrObject): List<ClassId>

    fun findField(classOrObject: KtClassOrObject, name: String): JavaField?

    fun findField(ktFile: KtFile?, name: String): JavaField?
}