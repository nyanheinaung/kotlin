/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused") // an API

package org.jetbrains.kotlin.script.util.templates

import org.jetbrains.kotlin.script.util.FilesAndMavenResolver
import org.jetbrains.kotlin.script.util.LocalFilesResolver
import kotlin.script.templates.ScriptTemplateDefinition

@ScriptTemplateDefinition(resolver = LocalFilesResolver::class, scriptFilePattern = ".*\\.kts")
abstract class StandardArgsScriptTemplateWithLocalResolving(val args: Array<String>)

@ScriptTemplateDefinition(resolver = FilesAndMavenResolver::class, scriptFilePattern = ".*\\.kts")
abstract class StandardArgsScriptTemplateWithMavenResolving(val args: Array<String>)

@ScriptTemplateDefinition(resolver = LocalFilesResolver::class, scriptFilePattern = ".*\\.kts")
abstract class BindingsScriptTemplateWithLocalResolving(val bindings: Map<String, Any?>)

@ScriptTemplateDefinition(resolver = FilesAndMavenResolver::class, scriptFilePattern = ".*\\.kts")
abstract class BindingsScriptTemplateWithMavenResolving(val bindings: Map<String, Any?>)
