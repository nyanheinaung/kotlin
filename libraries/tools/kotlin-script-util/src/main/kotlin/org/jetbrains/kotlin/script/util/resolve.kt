/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.script.util

import org.jetbrains.kotlin.script.InvalidScriptResolverAnnotation
import org.jetbrains.kotlin.script.util.resolvers.*
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull
import java.io.File
import java.util.concurrent.Future
import kotlin.script.dependencies.KotlinScriptExternalDependencies
import kotlin.script.dependencies.ScriptContents
import kotlin.script.dependencies.ScriptDependenciesResolver
import kotlin.script.dependencies.asFuture
import kotlin.script.templates.AcceptedAnnotations

open class KotlinAnnotatedScriptDependenciesResolver(val baseClassPath: List<File>, resolvers: Iterable<Resolver>)
    : ScriptDependenciesResolver
{
    private val resolvers: MutableList<Resolver> = resolvers.toMutableList()

    inner class ResolvedDependencies(previousDependencies: KotlinScriptExternalDependencies?, depsFromAnnotations: List<File> ) : KotlinScriptExternalDependencies {
        override val classpath = if (resolvers.isEmpty()) baseClassPath  else baseClassPath + depsFromAnnotations
        override val imports = if (previousDependencies != null) emptyList() else listOf(DependsOn::class.java.`package`.name + ".*")
    }

    @AcceptedAnnotations(DependsOn::class, Repository::class)
    override fun resolve(script: ScriptContents,
                         environment: Map<String, Any?>?,
                         report: (ScriptDependenciesResolver.ReportSeverity, String, ScriptContents.Position?) -> Unit,
                         previousDependencies: KotlinScriptExternalDependencies?
    ): Future<KotlinScriptExternalDependencies?> {
        val depsFromAnnotations: List<File> = resolveFromAnnotations(script)
        return (if (previousDependencies != null && depsFromAnnotations.isEmpty()) previousDependencies
                else ResolvedDependencies(previousDependencies, depsFromAnnotations)
               ).asFuture()
    }

    private fun resolveFromAnnotations(script: ScriptContents): List<File> {
        script.annotations.forEach { annotation ->
            when (annotation) {
                is Repository -> {
                    val isFlat: Boolean = resolvers.firstIsInstanceOrNull<FlatLibDirectoryResolver>()?.tryAddRepo(annotation)
                        ?: (FlatLibDirectoryResolver.tryCreate(annotation)?.also { resolvers.add(it) } != null)
                    if (!isFlat) {
                        resolvers.find { it !is FlatLibDirectoryResolver && it.tryAddRepo(annotation) }
                            ?: throw IllegalArgumentException("Illegal argument for Repository annotation: $annotation")
                    }
                }
                is DependsOn -> {}
                is InvalidScriptResolverAnnotation -> throw Exception("Invalid annotation ${annotation.name}", annotation.error)
                else -> throw Exception("Unknown annotation ${annotation.javaClass}")
            }
        }
        return script.annotations.filterIsInstance(DependsOn::class.java).flatMap { dep ->
            resolvers.asSequence().mapNotNull { it.tryResolve(dep) }.firstOrNull() ?:
                    throw Exception("Unable to resolve dependency $dep")
        }
    }
}

class LocalFilesResolver :
        KotlinAnnotatedScriptDependenciesResolver(emptyList(), arrayListOf(DirectResolver()))

class FilesAndMavenResolver :
        KotlinAnnotatedScriptDependenciesResolver(emptyList(), arrayListOf(DirectResolver(), MavenResolver()))

