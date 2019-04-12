/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package samples.collections

import samples.Sample
import samples.assertPrints

class ReversedViews {
    @Sample
    fun asReversedList() {
        val original = mutableListOf('a', 'b', 'c', 'd', 'e')
        val originalReadOnly = original as List<Char>
        val reversed = originalReadOnly.asReversed()

        assertPrints(original, "[a, b, c, d, e]")
        assertPrints(reversed, "[e, d, c, b, a]")

        // changing the original list affects its reversed view
        original.add('f')
        assertPrints(original, "[a, b, c, d, e, f]")
        assertPrints(reversed, "[f, e, d, c, b, a]")

        original[original.lastIndex] = 'z'
        assertPrints(original, "[a, b, c, d, e, z]")
        assertPrints(reversed, "[z, e, d, c, b, a]")
    }

    @Sample
    fun asReversedMutableList() {
        val original = mutableListOf(1, 2, 3, 4, 5)
        val reversed = original.asReversed()

        assertPrints(original, "[1, 2, 3, 4, 5]")
        assertPrints(reversed, "[5, 4, 3, 2, 1]")

        // changing the reversed view affects the original list
        reversed.add(0)
        assertPrints(original, "[0, 1, 2, 3, 4, 5]")
        assertPrints(reversed, "[5, 4, 3, 2, 1, 0]")

        // changing the original list affects its reversed view
        original[2] = -original[2]
        assertPrints(original, "[0, 1, -2, 3, 4, 5]")
        assertPrints(reversed, "[5, 4, 3, -2, 1, 0]")
    }
}