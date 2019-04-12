/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.text.test

import org.junit.Test
import kotlin.test.*

class RegexTest {
    @Test fun namedGroups() {
        val input = "1a 2b 3"
        val regex = "(?<num>\\d)(?<liter>\\w)?".toRegex()

        val matches = regex.findAll(input).toList()
        assertTrue(matches.all { it.groups.size == 3 })
        val (m1, m2, m3) = matches

        assertEquals("1", m1.groups["num"]?.value)
        assertEquals(0..0, m1.groups["num"]?.range)
        assertEquals("a", m1.groups["liter"]?.value)
        assertEquals(1..1, m1.groups["liter"]?.range)

        assertEquals("2", m2.groups["num"]?.value)
        assertEquals(3..3, m2.groups["num"]?.range)
        assertEquals("b", m2.groups["liter"]?.value)
        assertEquals(4..4, m2.groups["liter"]?.range)

        assertEquals("3", m3.groups["num"]?.value)
        assertNull(m3.groups["liter"])

        assertFailsWith<IllegalArgumentException> { m2.groups["unknown_group"] }.let { e ->
            assertTrue("unknown_group" in e.message!!)
        }
    }
}
