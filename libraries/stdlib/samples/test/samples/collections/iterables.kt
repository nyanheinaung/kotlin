/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package samples.collections

import samples.*

@RunWith(Enclosed::class)
class Iterables {

    class Building {

        @Sample
        fun iterable() {
            val iterable = Iterable {
                iterator {
                    yield(42)
                    yieldAll(1..5 step 2)
                }
            }
            val result = iterable.mapIndexed { index, value -> "$index: $value" }
            assertPrints(result, "[0: 42, 1: 1, 2: 3, 3: 5]")

            // can be iterated many times
            repeat(2) {
                val sum = iterable.sum()
                assertPrints(sum, "51")
            }
        }

    }

    class Operations {

        @Sample
        fun flattenIterable() {
            val deepList = listOf(listOf(1), listOf(2, 3), listOf(4, 5, 6))
            assertPrints(deepList.flatten(), "[1, 2, 3, 4, 5, 6]")
        }

        @Sample
        fun unzipIterable() {
            val list = listOf(1 to 'a', 2 to 'b', 3 to 'c')
            assertPrints(list.unzip(), "([1, 2, 3], [a, b, c])")
        }

        @Sample
        fun zipIterable() {
            val listA = listOf("a", "b", "c")
            val listB = listOf(1, 2, 3, 4)
            assertPrints(listA zip listB, "[(a, 1), (b, 2), (c, 3)]")
        }

        @Sample
        fun zipIterableWithTransform() {
            val listA = listOf("a", "b", "c")
            val listB = listOf(1, 2, 3, 4)
            val result = listA.zip(listB) { a, b -> "$a$b" }
            assertPrints(result, "[a1, b2, c3]")
        }
    }
}