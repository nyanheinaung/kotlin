/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations

import org.jetbrains.kotlin.builtins.KotlinBuiltIns
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.constants.ConstantValue
import org.jetbrains.kotlin.types.KotlinType
import kotlin.LazyThreadSafetyMode.PUBLICATION

class BuiltInAnnotationDescriptor(
        private val builtIns: KotlinBuiltIns,
        override val fqName: FqName,
        override val allValueArguments: Map<Name, ConstantValue<*>>
) : AnnotationDescriptor {
    override val type: KotlinType by lazy(PUBLICATION) {
        builtIns.getBuiltInClassByFqName(fqName).defaultType
    }

    override val source: SourceElement
        get() = SourceElement.NO_SOURCE
}
