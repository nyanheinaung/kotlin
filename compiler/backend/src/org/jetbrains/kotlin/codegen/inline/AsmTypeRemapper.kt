/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen.inline

import org.jetbrains.org.objectweb.asm.commons.Remapper
import org.jetbrains.org.objectweb.asm.commons.SignatureRemapper
import org.jetbrains.org.objectweb.asm.signature.SignatureReader
import org.jetbrains.org.objectweb.asm.signature.SignatureVisitor

class AsmTypeRemapper(val typeRemapper: TypeRemapper, val result: InlineResult) : Remapper() {
    override fun map(type: String): String {
        return typeRemapper.map(type)
    }

    override fun createSignatureRemapper(v: SignatureVisitor?): SignatureVisitor {
        return object : SignatureRemapper(v, this) {
            override fun visitTypeVariable(name: String) {
                /*TODO try to erase absent type variable*/
                val mapping = typeRemapper.mapTypeParameter(name) ?: return super.visitTypeVariable(name)

                if (mapping.newName != null) {
                    if (mapping.isReified) {
                        result.reifiedTypeParametersUsages.addUsedReifiedParameter(mapping.newName)
                    }
                    return super.visitTypeVariable(mapping.newName)
                }
                // else TypeVariable is replaced by concrete type
                SignatureReader(mapping.signature).accept(v)
            }

            override fun visitFormalTypeParameter(name: String) {
                typeRemapper.registerTypeParameter(name)
                super.visitFormalTypeParameter(name)
            }
        }
    }
}
