/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm

import org.jetbrains.kotlin.backend.jvm.codegen.ClassCodegen
import org.jetbrains.kotlin.ir.declarations.IrClass

class JvmCodegen(val context: JvmBackendContext) {
    fun generateClass(irClass: IrClass) {
        ClassCodegen.generate(irClass, context)
    }
}
