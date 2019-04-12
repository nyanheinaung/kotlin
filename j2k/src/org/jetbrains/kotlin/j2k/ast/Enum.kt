/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.j2k.ast

import org.jetbrains.kotlin.j2k.CodeBuilder

class Enum(
        name: Identifier,
        annotations: Annotations,
        modifiers: Modifiers,
        typeParameterList: TypeParameterList,
        implementsTypes: List<Type>,
        body: ClassBody
) : Class(name, annotations, modifiers, typeParameterList, emptyList(), null, implementsTypes, body) {

    override fun generateCode(builder: CodeBuilder) {
        builder append annotations appendWithSpaceAfter presentationModifiers() append "enum class " append name

        if (body.primaryConstructorSignature != null) {
            builder.append(body.primaryConstructorSignature)
        }

        builder append typeParameterList

        appendBaseTypes(builder)

        body.appendTo(builder)
    }
}
