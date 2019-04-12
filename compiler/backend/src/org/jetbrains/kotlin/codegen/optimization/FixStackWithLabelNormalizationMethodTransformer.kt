/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.optimization

import org.jetbrains.kotlin.codegen.optimization.fixStack.FixStackMethodTransformer
import org.jetbrains.kotlin.codegen.optimization.transformer.CompositeMethodTransformer

class FixStackWithLabelNormalizationMethodTransformer : CompositeMethodTransformer(
    LabelNormalizationMethodTransformer(),
    FixStackMethodTransformer()
)