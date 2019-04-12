/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.example.lib

import org.junit.Test

class TestWithJava {
	@Test
	fun testJavaClass() {
		JavaClass()
		f()
	}

	companion object {
		val seesJavaTestClass = TestJava::class
	}
}