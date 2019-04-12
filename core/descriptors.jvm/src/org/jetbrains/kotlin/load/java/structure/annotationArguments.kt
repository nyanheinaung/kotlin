/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

interface JavaAnnotationArgument {
    val name: Name?
}

interface JavaLiteralAnnotationArgument : JavaAnnotationArgument {
    val value: Any?
}

interface JavaArrayAnnotationArgument : JavaAnnotationArgument {
    fun getElements(): List<JavaAnnotationArgument>
}

interface JavaEnumValueAnnotationArgument : JavaAnnotationArgument {
    val enumClassId: ClassId?
    val entryName: Name?
}

interface JavaClassObjectAnnotationArgument : JavaAnnotationArgument {
    fun getReferencedType(): JavaType
}

interface JavaAnnotationAsAnnotationArgument : JavaAnnotationArgument {
    fun getAnnotation(): JavaAnnotation
}
