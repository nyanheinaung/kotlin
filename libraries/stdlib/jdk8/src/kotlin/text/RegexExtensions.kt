/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
@file:JvmName("RegexExtensionsJDK8Kt")
@file:kotlin.jvm.JvmPackageName("kotlin.text.jdk8")
package kotlin.text

/**
 * Returns a named group with the specified [name].
 *
 * @return An instance of [MatchGroup] if the group with the specified [name] was matched or `null` otherwise.
 * @throws IllegalArgumentException if there is no group with the specified [name] defined in the regex pattern.
 * @throws UnsupportedOperationException if getting named groups isn't supported on the current platform.
 */
@SinceKotlin("1.2")
public operator fun MatchGroupCollection.get(name: String): MatchGroup? {
    val namedGroups = this as? MatchNamedGroupCollection
            ?: throw UnsupportedOperationException("Retrieving groups by name is not supported on this platform.")

    return namedGroups[name]
}
