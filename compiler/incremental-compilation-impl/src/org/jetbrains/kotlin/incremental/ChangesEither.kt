/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.incremental

import org.jetbrains.kotlin.name.FqName

internal sealed class ChangesEither {
    internal class Known(
            val lookupSymbols: Collection<LookupSymbol> = emptyList(),
            val fqNames: Collection<FqName> = emptyList()
    ) : ChangesEither()
    internal class Unknown(val reason: String? = null) : ChangesEither()
}