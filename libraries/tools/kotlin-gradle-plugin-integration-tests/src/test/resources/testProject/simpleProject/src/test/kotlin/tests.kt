/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package demo

import com.google.common.primitives.Ints
import com.google.common.base.Joiner
import org.testng.Assert.*
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test as test

class TestSource() {
    @test fun f() {
        val example : KotlinGreetingJoiner = KotlinGreetingJoiner(Greeter("Hi"))
        example.addName("Harry")
        example.addName("Ron")
        example.addName(null)
        example.addName("Hermione")

        assertEquals(example.getJoinedGreeting(), "Hi Harry and Ron and Hermione")
    }
}

