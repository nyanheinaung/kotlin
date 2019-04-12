/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.reflect.jvm.internal.components

import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.SourceFile

class ReflectAnnotationSource(val annotation: Annotation) : SourceElement {
    override fun getContainingFile(): SourceFile = SourceFile.NO_SOURCE_FILE
}
