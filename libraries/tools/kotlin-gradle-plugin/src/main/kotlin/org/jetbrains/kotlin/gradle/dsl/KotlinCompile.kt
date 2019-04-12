/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.dsl

import groovy.lang.Closure
import org.gradle.api.Task

interface KotlinJsCompile : KotlinCompile<KotlinJsOptions>

interface KotlinJvmCompile : KotlinCompile<KotlinJvmOptions>

interface KotlinCommonCompile : KotlinCompile<KotlinMultiplatformCommonOptions>

interface KotlinJsDce : Task {
    val dceOptions: KotlinJsDceOptions

    val keep: MutableList<String>

    fun dceOptions(fn: KotlinJsDceOptions.() -> Unit) {
        dceOptions.fn()
    }

    fun dceOptions(fn: Closure<*>) {
        fn.delegate = dceOptions
        fn.call()
    }

    fun keep(vararg fqn: String)
}