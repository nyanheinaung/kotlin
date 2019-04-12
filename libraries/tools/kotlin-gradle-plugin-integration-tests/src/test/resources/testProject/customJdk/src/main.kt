/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// available in JDK 1.7
fun java.lang.AutoCloseable.silentClose() {

}

// available in JDK 1.8, should be an error with JDK 1.7
fun <T> java.util.stream.Stream<T>.count(): Int {
    return 0
}