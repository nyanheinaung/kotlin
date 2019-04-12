/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package example.library

import org.w3c.dom.Text
import kotlin.browser.*

public class Counter(val el: Text) {
    fun step(n: Int) {
        document.title = "Counter: ${n}"
        window.setTimeout({step(n+1)}, 1000)
    }

    fun start() {
        step(0)
    }
}
