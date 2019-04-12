/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.builtins

import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor

interface BuiltInsPackageFragment : PackageFragmentDescriptor {
    /**
     * `true` if this package fragment is loaded during compilation from the current compiler's class loader.
     * This fallback package fragment is useful because standard types like kotlin.String, kotlin.Unit will be resolved to something
     * even in the case of absence of kotlin-stdlib in the module dependencies. So, the code in the compiler that relies on these types
     * to be always there, will not crash.
     * However, if anything in the source code references anything from the fallback package, this is a compilation error because it means
     * that there's no kotlin-stdlib in the dependencies and the build is compiler version-specific, which could lead to weird issues.
     */
    val isFallback: Boolean
}
