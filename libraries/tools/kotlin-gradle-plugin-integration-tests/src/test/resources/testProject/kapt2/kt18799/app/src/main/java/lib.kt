/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package com.lib

@Retention(AnnotationRetention.SOURCE)
@kotlin.annotation.Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class Factory(val factoryClass: String, val something: Array<Test> = arrayOf())

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
annotation class Test
