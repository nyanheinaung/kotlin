/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.serialization.deserialization

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import java.io.InputStream

interface KotlinMetadataFinder {
    /**
     * @return an [InputStream] which should be used to load the .kotlin_metadata file for class with the given [classId].
     * [classId] identifies either a real top level class, or a package part (e.g. it can be "foo/bar/_1Kt")
     */
    fun findMetadata(classId: ClassId): InputStream?

    /**
     * @return `true` iff this finder is able to locate the package with the given [fqName], containing .kotlin_metadata files.
     * Note that returning `true` makes [MetadataPackageFragmentProvider] construct the package fragment for the package,
     * and that fact can alter the qualified name expression resolution in the compiler front-end
     */
    fun hasMetadataPackage(fqName: FqName): Boolean

    /**
     * @return an [InputStream] which should be used to load the .kotlin_builtins file for package with the given [packageFqName].
     */
    fun findBuiltInsData(packageFqName: FqName): InputStream?
}
