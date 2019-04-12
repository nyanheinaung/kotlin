/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package demo

import org.testng.Assert.assertEquals
import org.testng.annotations.Test as test

class TestKotlinGreetingJoiner() {
    @test
    fun test() {
        val example : KotlinGreetingJoiner = KotlinGreetingJoiner(Greeter("Hi"))
        example.addName("Harry")
        example.addName("Ron")
        example.addName("Hermione")

        assertEquals(example.getJoinedGreeting(), "Hi Harry and Ron and Hermione")
    }
}

