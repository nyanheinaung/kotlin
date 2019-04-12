/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.example

import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.stream.appendHTML

fun hello() = "hello"

fun main() {
    println(
            StringBuilder().appendHTML().html {
                body {
                    div {
                        a("https://kotlinlang.org") {
                            hello()
                        }
                    }
                }
            }
    )
}