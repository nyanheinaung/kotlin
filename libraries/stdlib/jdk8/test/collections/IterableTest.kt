/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.jdk8.collections.test

import org.junit.Test
import kotlin.test.*
import java.util.function.*

class IterableTest {

    val data = listOf("foo", "bar")
    val iterable = Iterable { data.iterator() }

    @Test fun spliterator() {
        val spliterator = iterable.spliterator()

        assertEquals(-1, spliterator.exactSizeIfKnown)
        val expected = data.toMutableList()
        spliterator.forEachRemaining {
            assertEquals(expected.removeAt(0), it)
        }
    }

    @Test fun forEach() {
        val expected = data.toMutableList()
        iterable.forEach(Consumer {
            assertEquals(expected.removeAt(0), it)
        })
    }

    @Test fun forEachRemaining() {
        val expected = data.toMutableList()
        val iterator = iterable.iterator()
        iterator.forEachRemaining(Consumer {
            assertEquals(expected.removeAt(0), it)
        })
    }

}
