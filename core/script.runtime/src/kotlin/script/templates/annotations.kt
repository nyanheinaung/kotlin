/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("unused")

package kotlin.script.templates

import kotlin.reflect.KClass
import kotlin.script.dependencies.ScriptDependenciesResolver
import kotlin.script.experimental.dependencies.DependenciesResolver.NoDependencies

const val DEFAULT_SCRIPT_FILE_PATTERN = ".*\\.kts"

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ScriptTemplateDefinition(val resolver: KClass<out ScriptDependenciesResolver> = NoDependencies::class,
                                          val scriptFilePattern: String = DEFAULT_SCRIPT_FILE_PATTERN)

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AcceptedAnnotations(vararg val supportedAnnotationClasses: KClass<out Annotation>)
