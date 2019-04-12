/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER", "CANNOT_OVERRIDE_INVISIBLE_MEMBER")
package kotlin.internal.jdk8

import java.util.regex.MatchResult
import java.util.regex.Matcher
import kotlin.internal.PlatformImplementations
import kotlin.internal.jdk7.JDK7PlatformImplementations
import kotlin.random.Random
import kotlin.random.jdk8.PlatformThreadLocalRandom

internal open class JDK8PlatformImplementations : JDK7PlatformImplementations() {

    override fun getMatchResultNamedGroup(matchResult: MatchResult, name: String): MatchGroup? {
        val matcher = matchResult as? Matcher ?: throw UnsupportedOperationException("Retrieving groups by name is not supported on this platform.")

        val range = matcher.start(name)..matcher.end(name) - 1
        return if (range.start >= 0)
            MatchGroup(matcher.group(name), range)
        else
            null
    }

    override fun defaultPlatformRandom(): Random = PlatformThreadLocalRandom()

}
