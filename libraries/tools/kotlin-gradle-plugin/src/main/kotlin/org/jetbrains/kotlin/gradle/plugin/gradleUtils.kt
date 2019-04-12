/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin

import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.HasConvention
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.compilerRunner.KotlinLogger
import java.io.File

internal fun AbstractCompile.appendClasspathDynamically(file: File) {
    var added = false

    doFirst {
        if (file !in classpath) {
            classpath += project.files(file)
            added = true
        }
    }
    doLast {
        if (added) {
            classpath -= project.files(file)
        }
    }
}

fun AbstractCompile.mapClasspath(fn: () -> FileCollection) {
    conventionMapping.map("classpath", fn)
}

internal inline fun <reified T : Any> Any.addConvention(name: String, plugin: T) {
    (this as HasConvention).convention.plugins[name] = plugin
}

internal inline fun <reified T : Any> Any.addExtension(name: String, extension: T) =
    (this as ExtensionAware).extensions.add(name, extension)

internal fun Any.getConvention(name: String): Any? =
    (this as HasConvention).convention.plugins[name]