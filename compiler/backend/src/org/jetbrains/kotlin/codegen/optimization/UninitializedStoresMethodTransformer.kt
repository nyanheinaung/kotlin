/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization

import org.jetbrains.kotlin.codegen.coroutines.UninitializedStoresProcessor
import org.jetbrains.kotlin.codegen.optimization.transformer.MethodTransformer
import org.jetbrains.kotlin.config.JVMConstructorCallNormalizationMode
import org.jetbrains.org.objectweb.asm.tree.MethodNode

class UninitializedStoresMethodTransformer(
    private val mode: JVMConstructorCallNormalizationMode
) : MethodTransformer() {

    override fun transform(internalClassName: String, methodNode: MethodNode) {
        if (mode.isEnabled) {
            UninitializedStoresProcessor(methodNode, mode.shouldPreserveClassInitialization).run()
        }
    }

}