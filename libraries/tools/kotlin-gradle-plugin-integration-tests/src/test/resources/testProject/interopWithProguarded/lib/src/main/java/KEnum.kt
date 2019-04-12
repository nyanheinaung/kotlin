/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kenum

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

enum class KEnum(@Foo val foo: String) {
    OK("123");

    @Retention(RetentionPolicy.RUNTIME)
    annotation class Foo {}
}
