/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

import kotlin.js.Console

import com.example.lib.*

external val console: Console

fun nodeJsMain(args: Array<String>) {
	console.info(id(123), idUsage(), expectedFun())
}