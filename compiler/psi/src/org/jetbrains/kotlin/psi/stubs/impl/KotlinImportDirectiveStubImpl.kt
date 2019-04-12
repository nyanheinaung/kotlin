/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes

class KotlinImportDirectiveStubImpl(
    parent: StubElement<*>,
    private val isAllUnder: Boolean,
    private val importedFqName: StringRef?,
    private val isValid: Boolean
) : KotlinStubBaseImpl<KtImportDirective>(parent, KtStubElementTypes.IMPORT_DIRECTIVE), KotlinImportDirectiveStub {
    override fun isAllUnder(): Boolean = isAllUnder

    override fun getImportedFqName(): FqName? {
        val fqNameString = StringRef.toString(importedFqName)
        return if (fqNameString != null) FqName(fqNameString) else null
    }

    override fun isValid(): Boolean = isValid
}
