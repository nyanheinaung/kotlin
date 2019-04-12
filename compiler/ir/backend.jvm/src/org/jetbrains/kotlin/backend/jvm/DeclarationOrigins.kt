/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm

import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOriginImpl
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.IrStatementOriginImpl

interface JvmLoweredDeclarationOrigin : IrDeclarationOrigin {
    object CLASS_STATIC_INITIALIZER : IrDeclarationOriginImpl("CLASS_STATIC_INITIALIZER")
    object DEFAULT_IMPLS : IrDeclarationOriginImpl("DEFAULT_IMPL")
    object FIELD_FOR_OUTER_THIS : IrDeclarationOriginImpl("FIELD_FOR_OUTER_THIS")
    object FUNCTION_REFERENCE_IMPL : IrDeclarationOriginImpl("FUNCTION_REFERENCE_IMPL")
    object SYNTHETIC_ACCESSOR : IrDeclarationOriginImpl("SYNTHETIC_ACCESSOR", isSynthetic = true)
    object TO_ARRAY : IrDeclarationOriginImpl("TO_ARRAY")
    object JVM_STATIC_WRAPPER : IrDeclarationOriginImpl("JVM_STATIC_WRAPPER")
    object JVM_STATIC_WRAPPER_SYNTHETIC : IrDeclarationOriginImpl("JVM_STATIC_WRAPPER_SYNTHETIC", isSynthetic = true)
    object JVM_OVERLOADS_WRAPPER : IrDeclarationOriginImpl("JVM_OVERLOADS_WRAPPER")
    object SYNTHETIC_METHOD_FOR_PROPERTY_ANNOTATIONS :
        IrDeclarationOriginImpl("SYNTHETIC_METHOD_FOR_PROPERTY_ANNOTATIONS", isSynthetic = true)
    object GENERATED_PROPERTY_REFERENCE : IrDeclarationOriginImpl("GENERATED_PROPERTY_REFERENCE", isSynthetic = true)
    object GENERATED_SAM_IMPLEMENTATION : IrDeclarationOriginImpl("GENERATED_SAM_IMPLEMENTATION", isSynthetic = true)
}

interface JvmLoweredStatementOrigin : IrStatementOrigin {
    object DEFAULT_IMPLS_DELEGATION : IrStatementOriginImpl("DEFAULT_IMPL_DELEGATION")
    object TO_ARRAY : IrDeclarationOriginImpl("TO_ARRAY")
}
