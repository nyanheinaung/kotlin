/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.descriptors.annotations

enum class AnnotationUseSiteTarget(renderName: String? = null) {
    FIELD(),
    FILE(),
    PROPERTY(),
    PROPERTY_GETTER("get"),
    PROPERTY_SETTER("set"),
    RECEIVER(),
    CONSTRUCTOR_PARAMETER("param"),
    SETTER_PARAMETER("setparam"),
    PROPERTY_DELEGATE_FIELD("delegate");

    val renderName: String = renderName ?: name.toLowerCase()
}
