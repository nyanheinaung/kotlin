/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.j2k

data class ConverterSettings(
        var forceNotNullTypes: Boolean,
        var specifyLocalVariableTypeByDefault: Boolean,
        var specifyFieldTypeByDefault: Boolean,
        var openByDefault: Boolean,
        var noInternalForMembersOfInternal: Boolean
) {

    companion object {
        val defaultSettings: ConverterSettings = ConverterSettings(
                forceNotNullTypes = true,
                specifyLocalVariableTypeByDefault = false,
                specifyFieldTypeByDefault = false,
                openByDefault = false,
                noInternalForMembersOfInternal = true
        )
    }
}
