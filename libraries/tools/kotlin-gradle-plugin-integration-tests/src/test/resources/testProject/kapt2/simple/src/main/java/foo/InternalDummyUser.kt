/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package foo

internal class InternalDummyUser {
    internal fun use(dummy: InternalDummy) {
        if (dummy.x != "InternalDummy.x") throw AssertionError("dummy.x = ${dummy.x}")
    }
}