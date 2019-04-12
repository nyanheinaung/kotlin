/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl

internal class FictitiousArrayConstructor(arrayClass: ClassDescriptor) : SimpleFunctionDescriptorImpl(
    arrayClass.containingDeclaration, null, Annotations.EMPTY, arrayClass.name, CallableMemberDescriptor.Kind.SYNTHESIZED,
    SourceElement.NO_SOURCE
) {
    companion object Factory {
        @JvmStatic
        fun create(arrayConstructor: ConstructorDescriptor): FictitiousArrayConstructor {
            val arrayClass = arrayConstructor.constructedClass
            return FictitiousArrayConstructor(arrayClass).apply {
                this.initialize(
                    null, null, arrayConstructor.typeParameters, arrayConstructor.valueParameters, arrayClass.defaultType,
                    Modality.FINAL, Visibilities.PUBLIC
                )
                this.isInline = true
            }
        }
    }
}
