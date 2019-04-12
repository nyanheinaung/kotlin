/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.structure

import org.jetbrains.kotlin.builtins.PrimitiveType

interface JavaType

interface JavaArrayType : JavaType {
    val componentType: JavaType
}

interface JavaClassifierType : JavaType, JavaAnnotationOwner {
    val classifier: JavaClassifier?
    val typeArguments: List<JavaType?>

    val isRaw: Boolean

    val classifierQualifiedName: String
    val presentableText: String
}

interface JavaPrimitiveType : JavaType {
    /** `null` means the `void` type. */
    val type: PrimitiveType?
}

interface JavaWildcardType : JavaType {
    val bound: JavaType?
    val isExtends: Boolean
}
