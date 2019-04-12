/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.repl

import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read

open class GenericReplEvaluatorState(baseClasspath: Iterable<File>, baseClassloader: ClassLoader?, override val lock: ReentrantReadWriteLock = ReentrantReadWriteLock())
    : IReplStageState<EvalClassWithInstanceAndLoader>
{
    override val history: IReplStageHistory<EvalClassWithInstanceAndLoader> = BasicReplStageHistory(lock)

    override val currentGeneration: Int get() = (history as BasicReplStageHistory<*>).currentGeneration.get()

    val topClassLoader: ReplClassLoader = makeReplClassLoader(baseClassloader, baseClasspath)

    val currentClasspath: List<File> get() = lock.read {
        history.peek()?.item?.classLoader?.listAllUrlsAsFiles()
        ?: topClassLoader.listAllUrlsAsFiles()
    }
}

internal fun makeReplClassLoader(baseClassloader: ClassLoader?, baseClasspath: Iterable<File>) =
        ReplClassLoader(URLClassLoader(baseClasspath.map { it.toURI().toURL() }.toTypedArray(), baseClassloader))
