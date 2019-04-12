/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package samples.properties

import kotlin.properties.Delegates
import samples.*
import kotlin.test.*

class Delegates {
    @Sample
    fun vetoableDelegate() {
        var max: Int by Delegates.vetoable(0) { property, oldValue, newValue ->
            newValue > oldValue
        }

        assertPrints(max, "0")

        max = 10
        assertPrints(max, "10")

        max = 5
        assertPrints(max, "10")
    }

    @Sample
    fun throwVetoableDelegate() {
        var max: Int by Delegates.vetoable(0) { property, oldValue, newValue ->
            if (newValue > oldValue) true else throw IllegalArgumentException("New value must be larger than old value.")
        }

        assertPrints(max, "0")

        max = 10
        assertPrints(max, "10")

        assertFailsWith<IllegalArgumentException> { max = 5 }
    }

    @Sample
    fun notNullDelegate() {
        var max: Int by Delegates.notNull()

        assertFailsWith<IllegalStateException> { println(max) }

        max = 10
        assertPrints(max, "10")
    }

    @Sample
    fun observableDelegate() {
        var observed = false
        var max: Int by Delegates.observable(0) { property, oldValue, newValue ->
            observed = true
        }

        assertPrints(max, "0")
        assertFalse(observed)

        max = 10
        assertPrints(max, "10")
        assertTrue(observed)
    }
}