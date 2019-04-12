/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi.stubs.impl

import com.intellij.psi.PsiClass
import com.intellij.psi.impl.java.stubs.PsiClassStub
import com.intellij.psi.stubs.PsiClassHolderFileStub
import com.intellij.psi.stubs.PsiFileStubImpl
import com.intellij.psi.tree.IStubFileElementType
import com.intellij.util.io.StringRef
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.stubs.KotlinFileStub
import org.jetbrains.kotlin.psi.stubs.KotlinImportAliasStub
import org.jetbrains.kotlin.psi.stubs.KotlinImportDirectiveStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes.FILE
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes.IMPORT_LIST
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

open class KotlinFileStubImpl(
    jetFile: KtFile?,
    private val packageName: StringRef,
    private val isScript: Boolean
) : PsiFileStubImpl<KtFile>(jetFile), KotlinFileStub, PsiClassHolderFileStub<KtFile> {

    constructor(jetFile: KtFile?, packageName: String, isScript: Boolean)
            : this(jetFile, StringRef.fromString(packageName)!!, isScript)

    override fun getPackageFqName(): FqName = FqName(StringRef.toString(packageName)!!)
    override fun isScript(): Boolean = isScript
    override fun getType(): IStubFileElementType<KotlinFileStub> = FILE

    override fun toString(): String = "PsiJetFileStubImpl[" + "package=" + getPackageFqName().asString() + "]"

    override fun getClasses(): Array<PsiClass> {
        return childrenStubs.filterIsInstance<PsiClassStub<*>>().map { it.psi }.toTypedArray()
    }

    override fun findImportsByAlias(alias: String): List<KotlinImportDirectiveStub> {
        val importList = childrenStubs.firstOrNull { it.stubType == IMPORT_LIST } ?: return emptyList()
        return importList.childrenStubs.filterIsInstance<KotlinImportDirectiveStub>().filter {
            it.childrenStubs.firstIsInstanceOrNull<KotlinImportAliasStub>()?.getName() == alias
        }
    }
}
