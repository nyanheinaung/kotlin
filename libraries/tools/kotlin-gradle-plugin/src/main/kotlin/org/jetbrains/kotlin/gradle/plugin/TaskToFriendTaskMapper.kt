/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Task
import org.jetbrains.annotations.TestOnly

internal abstract class TaskToFriendTaskMapper {
    operator fun get(task: Task): String? =
        getFriendByName(task.name)

    @TestOnly
    operator fun get(name: String): String? =
        getFriendByName(name)

    protected abstract fun getFriendByName(name: String): String?
}

sealed internal class RegexTaskToFriendTaskMapper(
    private val prefix: String,
    suffix: String,
    private val targetName: String,
    private val postfixReplacement: String
) : TaskToFriendTaskMapper() {
    class Default(targetName: String) : RegexTaskToFriendTaskMapper("compile", "TestKotlin", targetName, "Kotlin")
    class Android(targetName: String) : RegexTaskToFriendTaskMapper("compile", "(Unit|Android)TestKotlin", targetName, "Kotlin")

    private val regex = "$prefix(.*)$suffix${targetName.capitalize()}".toRegex()

    override fun getFriendByName(name: String): String? {
        val match = regex.matchEntire(name) ?: return null
        val variant = match.groups[1]?.value ?: ""
        return prefix + variant + postfixReplacement + targetName.capitalize()
    }
}