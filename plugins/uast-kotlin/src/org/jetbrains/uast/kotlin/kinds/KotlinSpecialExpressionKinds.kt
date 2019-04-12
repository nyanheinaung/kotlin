/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.uast.kotlin.kinds

import org.jetbrains.uast.UastSpecialExpressionKind

object KotlinSpecialExpressionKinds {
    @JvmField
    val WHEN = UastSpecialExpressionKind("when")

    @JvmField
    val WHEN_ENTRY = UastSpecialExpressionKind("when_entry")

    @JvmField
    val CLASS_BODY = UastSpecialExpressionKind("class_body")

    @JvmField
    val ELVIS = UastSpecialExpressionKind("elvis")

    @JvmField
    val SUPER_DELEGATION = UastSpecialExpressionKind("super_delegation")
}