/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization.transformer

import org.jetbrains.org.objectweb.asm.tree.MethodNode

open class CompositeMethodTransformer(private val transformers: List<MethodTransformer>) : MethodTransformer() {
    constructor(vararg transformers: MethodTransformer?) : this(transformers.filterNotNull())

    override fun transform(internalClassName: String, methodNode: MethodNode) {
        transformers.forEach { it.transform(internalClassName, methodNode) }
    }

    companion object {
        inline fun build(builder: MutableList<MethodTransformer>.() -> Unit) =
            CompositeMethodTransformer(ArrayList<MethodTransformer>().apply { builder() })
    }
}