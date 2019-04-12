/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations

import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass
import org.jetbrains.kotlin.resolve.descriptorUtil.fqNameOrNull
import org.jetbrains.kotlin.types.ErrorUtils
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.getAbbreviation

interface AnnotationDescriptor {
    val type: KotlinType

    val fqName: FqName?
        get() = annotationClass?.takeUnless(ErrorUtils::isError)?.fqNameOrNull()

    val allValueArguments: Map<Name, ConstantValue<*>>

    val source: SourceElement
}

val AnnotationDescriptor.abbreviationFqName: FqName?
    get() = type.getAbbreviation()?.constructor?.declarationDescriptor?.fqNameOrNull()
