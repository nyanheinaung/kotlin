/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon.client

import org.jetbrains.kotlin.cli.common.repl.*
import org.jetbrains.kotlin.daemon.common.ReplStateFacade
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock

// NOTE: the lock is local
// TODO: verify that locla lock doesn't lead to any synch problems
class RemoteReplCompilerStateHistory(private val state: RemoteReplCompilerState) : IReplStageHistory<Unit>, AbstractList<ReplHistoryRecord<Unit>>() {
    override val size: Int
        get() = state.replStateFacade.getHistorySize()

    override fun get(index: Int): ReplHistoryRecord<Unit> = ReplHistoryRecord(state.replStateFacade.historyGet(index), Unit)

    override fun push(id: ILineId, item: Unit) {
        throw NotImplementedError("push to remote history is not supported")
    }

    override fun pop(): ReplHistoryRecord<Unit>? {
        throw NotImplementedError("pop from remote history is not supported")
    }

    override fun reset(): Iterable<ILineId> = state.replStateFacade.historyReset().apply {
        currentGeneration.incrementAndGet()
    }

    override fun resetTo(id: ILineId): Iterable<ILineId> = state.replStateFacade.historyResetTo(id).apply {
        currentGeneration.incrementAndGet()
    }

    val currentGeneration = AtomicInteger(REPL_CODE_LINE_FIRST_GEN)

    override val lock: ReentrantReadWriteLock get() = state.lock
}

class RemoteReplCompilerState(internal val replStateFacade: ReplStateFacade, override val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()) : IReplStageState<Unit> {

    override val currentGeneration: Int get() = (history as RemoteReplCompilerStateHistory).currentGeneration.get()

    override val history: IReplStageHistory<Unit> = RemoteReplCompilerStateHistory(this)
}