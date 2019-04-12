/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.PsiElement
import com.intellij.psi.stubs.StubElement
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.stubs.KotlinClassStub
import org.jetbrains.kotlin.psi.stubs.elements.KtClassElementType
import java.util.*

class KotlinClassStubImpl(
    type: KtClassElementType,
    parent: StubElement<out PsiElement>?,
    private val qualifiedName: StringRef?,
    private val name: StringRef?,
    private val superNames: Array<StringRef>,
    private val isInterface: Boolean,
    private val isEnumEntry: Boolean,
    private val isLocal: Boolean,
    private val isTopLevel: Boolean
) : KotlinStubBaseImpl<KtClass>(parent, type), KotlinClassStub {

    override fun getFqName(): FqName? {
        val stringRef = StringRef.toString(qualifiedName) ?: return null
        return FqName(stringRef)
    }

    override fun isInterface() = isInterface
    override fun isEnumEntry() = isEnumEntry
    override fun isLocal() = isLocal
    override fun getName() = StringRef.toString(name)

    override fun getSuperNames(): List<String> {
        val result = ArrayList<String>()
        for (ref in superNames) {
            result.add(ref.toString())
        }
        return result
    }

    override fun isTopLevel() = isTopLevel
}
