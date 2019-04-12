/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core.util

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ModalityState
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.Runnable
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext


public object EDT : CoroutineDispatcher() {
    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return !ApplicationManager.getApplication().isDispatchThread
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        val modalityState = context[ModalityStateElement.Key]?.modalityState ?: ModalityState.defaultModalityState()
        ApplicationManager.getApplication().invokeLater(block, modalityState)
    }

    class ModalityStateElement(val modalityState: ModalityState) : AbstractCoroutineContextElement(Key) {
        companion object Key : CoroutineContext.Key<ModalityStateElement>
    }

    operator fun invoke(project: Project) = this + project.cancelOnDisposal
}

// job that is cancelled when the project is disposed
val Project.cancelOnDisposal: Job
    get() = ServiceManager.getService(this, ProjectJob::class.java).sharedJob

internal class ProjectJob(project: Project) {
    internal val sharedJob: Job = Job()

    init {
        Disposer.register(project, Disposable {
            sharedJob.cancel()
        })
    }
}