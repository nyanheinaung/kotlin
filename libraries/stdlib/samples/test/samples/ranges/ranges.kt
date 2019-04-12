/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package samples.ranges

import samples.*
import java.sql.Date
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Ranges {

    @Sample
    fun rangeFromComparable() {
        val start = Date.valueOf("2017-01-01")
        val end = Date.valueOf("2017-12-31")
        val range = start..end
        assertPrints(range, "2017-01-01..2017-12-31")

        assertTrue(Date.valueOf("2017-05-27") in range)
        assertFalse(Date.valueOf("2018-01-01") in range)
        assertTrue(Date.valueOf("2018-01-01") !in range)
    }

    @Sample
    fun rangeFromDouble() {
        val range = 1.0..100.0
        assertPrints(range, "1.0..100.0")

        assertTrue(3.14 in range)
        assertFalse(100.1 in range)
    }

    @Sample
    fun rangeFromFloat() {
        val range = 1f..100f
        assertPrints(range, "1.0..100.0")

        assertTrue(3.14f in range)
        assertFalse(100.1f in range)
    }
}