/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.types.SimpleType

interface LocalClassifierTypeSettings {
    val replacementTypeForLocalClassifiers: SimpleType?

    object Default : LocalClassifierTypeSettings {
        override val replacementTypeForLocalClassifiers: SimpleType?
            get() = null
    }
}
