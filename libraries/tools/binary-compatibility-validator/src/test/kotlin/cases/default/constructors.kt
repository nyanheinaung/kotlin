/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UNUSED_PARAMETER")
package cases.default

class ClassConstructors
internal constructor(name: String, flags: Int = 0) {

    internal constructor(name: StringBuilder, flags: Int = 0) : this(name.toString(), flags)

}

