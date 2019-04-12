/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.index

import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import java.util.*

interface JvmDependenciesIndex {
    val indexedRoots: Sequence<JavaRoot>

    fun <T : Any> findClass(
        classId: ClassId,
        acceptedRootTypes: Set<JavaRoot.RootType> = JavaRoot.SourceAndBinary,
        findClassGivenDirectory: (VirtualFile, JavaRoot.RootType) -> T?
    ): T?

    fun traverseDirectoriesInPackage(
        packageFqName: FqName,
        acceptedRootTypes: Set<JavaRoot.RootType> = JavaRoot.SourceAndBinary,
        continueSearch: (VirtualFile, JavaRoot.RootType) -> Boolean
    )
}

data class JavaRoot(val file: VirtualFile, val type: RootType, val prefixFqName: FqName? = null) {
    enum class RootType {
        SOURCE,
        BINARY
    }

    companion object RootTypes {
        val OnlyBinary: Set<RootType> = EnumSet.of(RootType.BINARY)
        val SourceAndBinary: Set<RootType> = EnumSet.of(RootType.BINARY, RootType.SOURCE)
    }
}
