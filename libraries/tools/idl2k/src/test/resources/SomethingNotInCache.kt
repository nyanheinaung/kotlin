/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

public external open class SomethingNotInCache {
    open val someReadOnlyParam: dynamic
    var someWriteableParam: dynamic
    fun someEmptyMethod(): String
    fun someMethod(root: dynamic): String
    fun optionalUsvStringFetcher(name: String): String?
}

