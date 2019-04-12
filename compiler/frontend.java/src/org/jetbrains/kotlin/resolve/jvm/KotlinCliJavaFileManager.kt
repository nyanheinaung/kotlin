/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.jvm

import com.intellij.psi.impl.file.impl.JavaFileManager
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.load.java.JavaClassFinder
import org.jetbrains.kotlin.load.java.structure.JavaClass
import org.jetbrains.kotlin.name.FqName

interface KotlinCliJavaFileManager : JavaFileManager {
    fun findClass(request: JavaClassFinder.Request, searchScope: GlobalSearchScope): JavaClass?
    fun knownClassNamesInPackage(packageFqName: FqName): Set<String>?
}
