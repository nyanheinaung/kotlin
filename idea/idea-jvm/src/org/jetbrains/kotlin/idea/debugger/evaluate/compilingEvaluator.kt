/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.debugger.evaluate.compilingEvaluator

import com.intellij.debugger.engine.evaluation.EvaluateException
import com.sun.jdi.ClassLoaderReference
import org.jetbrains.kotlin.idea.debugger.evaluate.ExecutionContext
import org.jetbrains.kotlin.idea.debugger.evaluate.LOG
import org.jetbrains.kotlin.idea.debugger.evaluate.classLoading.ClassLoadingAdapter
import org.jetbrains.kotlin.idea.debugger.evaluate.classLoading.ClassToLoad

fun loadClassesSafely(context: ExecutionContext, classes: Collection<ClassToLoad>): ClassLoaderReference? {
    return try {
        loadClasses(context, classes)
    } catch (e: EvaluateException) {
        throw e
    } catch (e: Throwable) {
        LOG.debug("Failed to evaluate expression", e)
        null
    }
}

fun loadClasses(context: ExecutionContext, classes: Collection<ClassToLoad>): ClassLoaderReference? {
    if (classes.isEmpty()) {
        return null
    }

    return ClassLoadingAdapter.loadClasses(context, classes)
}