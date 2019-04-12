/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:JvmName("AutoCloseableKt")
@file:kotlin.jvm.JvmPackageName("kotlin.jdk7")
package kotlin

/**
 * Executes the given [block] function on this resource and then closes it down correctly whether an exception
 * is thrown or not.
 *
 * In case if the resource is being closed due to an exception occurred in [block], and the closing also fails with an exception,
 * the latter is added to the [suppressed][java.lang.Throwable.addSuppressed] exceptions of the former.
 *
 * @param block a function to process this [AutoCloseable] resource.
 * @return the result of [block] function invoked on this resource.
 */
@SinceKotlin("1.2")
@kotlin.internal.InlineOnly
public inline fun <T : AutoCloseable?, R> T.use(block: (T) -> R): R {
    var exception: Throwable? = null
    try {
        return block(this)
    } catch (e: Throwable) {
        exception = e
        throw e
    } finally {
        this.closeFinally(exception)
    }
}

/**
 * Closes this [AutoCloseable], suppressing possible exception or error thrown by [AutoCloseable.close] function when
 * it's being closed due to some other [cause] exception occurred.
 *
 * The suppressed exception is added to the list of suppressed exceptions of [cause] exception.
 */
@SinceKotlin("1.2")
@PublishedApi
internal fun AutoCloseable?.closeFinally(cause: Throwable?) = when {
    this == null -> {}
    cause == null -> close()
    else ->
        try {
            close()
        } catch (closeException: Throwable) {
            cause.addSuppressed(closeException)
        }
}
