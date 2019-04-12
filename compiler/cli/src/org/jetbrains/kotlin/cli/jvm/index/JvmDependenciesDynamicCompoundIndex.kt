/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.index

import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class JvmDependenciesDynamicCompoundIndex : JvmDependenciesIndex {
    private val indices = arrayListOf<JvmDependenciesIndex>()
    private val lock = ReentrantReadWriteLock()

    fun addIndex(index: JvmDependenciesIndex) {
        lock.write {
            indices.add(index)
        }
    }

    fun addNewIndexForRoots(roots: Iterable<JavaRoot>): JvmDependenciesIndex? =
        lock.read {
            val alreadyIndexed = indexedRoots.toHashSet()
            val newRoots = roots.filter { root -> root !in alreadyIndexed }
            if (newRoots.isEmpty()) null
            else JvmDependenciesIndexImpl(newRoots).also(this::addIndex)
        }

    override val indexedRoots: Sequence<JavaRoot> get() = indices.asSequence().flatMap { it.indexedRoots }

    override fun <T : Any> findClass(
        classId: ClassId,
        acceptedRootTypes: Set<JavaRoot.RootType>,
        findClassGivenDirectory: (VirtualFile, JavaRoot.RootType) -> T?
    ): T? = lock.read {
        indices.asSequence().mapNotNull { it.findClass(classId, acceptedRootTypes, findClassGivenDirectory) }.firstOrNull()
    }

    override fun traverseDirectoriesInPackage(
        packageFqName: FqName,
        acceptedRootTypes: Set<JavaRoot.RootType>,
        continueSearch: (VirtualFile, JavaRoot.RootType) -> Boolean
    ) = lock.read {
        indices.forEach { it.traverseDirectoriesInPackage(packageFqName, acceptedRootTypes, continueSearch) }
    }
}
