/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package demo

import com.google.common.primitives.Ints
import com.google.common.base.Joiner

class ExampleSource(param : Int) {
  val property = param
  fun f() : String? {
    return "Hello World"
  }
}

