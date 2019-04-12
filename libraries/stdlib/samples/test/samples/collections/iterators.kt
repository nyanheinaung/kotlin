/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package samples.collections

import samples.Sample
import java.util.*

class Iterators {

    @Sample
    fun iteratorForEnumeration() {
        val vector = Vector<String>().apply {
            add("RED")
            add("GREEN")
            add("BLUE")
        }

        // iterator() extension is called here
        for (e in vector.elements()) {
            println("The element is $e")
        }
    }

    @Sample
    fun iterator() {
        val mutableList = mutableListOf(1, 2, 3)
        val mutableIterator = mutableList.iterator()

        // iterator() extension is called here
        for (e in mutableIterator) {
            if (e % 2 == 0) {
                // we can remove items from the iterator without getting ConcurrentModificationException
                // because it's the same iterator that is iterated with for loop
                mutableIterator.remove()
            }

            println("The element is $e")
        }
    }

    @Sample
    fun withIndexIterator() {
        val iterator = ('a'..'c').iterator()

        for ((index, value) in iterator.withIndex()) {
            println("The element at $index is $value")
        }
    }

    @Sample
    fun forEachIterator() {
        val iterator = (1..3).iterator()
        // skip an element
        if (iterator.hasNext()) {
            iterator.next()
        }

        // do something with the rest of elements
        iterator.forEach {
            println("The element is $it")
        }
    }
}