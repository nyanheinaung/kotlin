/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.j2k.ast

class Interface(
        name: Identifier,
        annotations: Annotations,
        modifiers: Modifiers,
        typeParameterList: TypeParameterList,
        extendsTypes: List<Type>,
        implementsTypes: List<Type>,
        body: ClassBody
) : Class(name, annotations, modifiers, typeParameterList, extendsTypes, null, implementsTypes, body) {

    override val keyword: String
        get() = "interface"

    override fun presentationModifiers(): Modifiers
            = modifiers.filter { it in ACCESS_MODIFIERS }
}
