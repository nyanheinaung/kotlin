/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.backend.ast

class JsImportedModule(val externalName: String, var internalName: JsName, val plainReference: JsExpression?) {
    val key = JsImportedModuleKey(externalName, plainReference?.toString())
}

data class JsImportedModuleKey(val baseName: String, val plainName: String?)