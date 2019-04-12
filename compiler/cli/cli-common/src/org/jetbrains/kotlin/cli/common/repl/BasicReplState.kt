/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.repl

import java.io.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

data class LineId(override val no: Int, override val generation: Int, private val codeHash: Int) : ILineId, Serializable {

    constructor(codeLine: ReplCodeLine): this(codeLine.no, codeLine.generation, codeLine.code.hashCode())

    override fun compareTo(other: ILineId): Int = (other as? LineId)?.let {
        no.compareTo(it.no).takeIf { it != 0 }
        ?: generation.compareTo(it.generation).takeIf { it != 0 }
        ?: codeHash.compareTo(it.codeHash)
    } ?: -1 // TODO: check if it doesn't break something

    companion object {
        private val serialVersionUID: Long = 8328353000L
    }
}

open class BasicReplStageHistory<T>(override val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()) : IReplStageHistory<T>, ArrayList<ReplHistoryRecord<T>>() {

    val currentGeneration = AtomicInteger(REPL_CODE_LINE_FIRST_GEN)

    override fun push(id: ILineId, item: T) {
        lock.write {
            add(ReplHistoryRecord(id, item))
        }
    }

    override fun pop(): ReplHistoryRecord<T>? = lock.write { if (isEmpty()) null else removeAt(lastIndex) }

    override fun reset(): Iterable<ILineId> {
        lock.write {
            val removed = map { it.id }
            clear()
            currentGeneration.incrementAndGet()
            return removed
        }
    }

    override fun resetTo(id: ILineId): Iterable<ILineId> {
        lock.write {
            val idx = indexOfFirst { it.id == id }
            if (idx < 0) throw java.util.NoSuchElementException("Cannot rest to inexistent line ${id.no}")
            return if (idx < lastIndex) {
                val removed = asSequence().drop(idx + 1).map { it.id }.toList()
                removeRange(idx + 1, size)
                currentGeneration.incrementAndGet()
                removed
            }
            else {
                currentGeneration.incrementAndGet()
                emptyList()
            }
        }
    }
}

open class BasicReplStageState<HistoryItemT>(override final val lock: ReentrantReadWriteLock = ReentrantReadWriteLock()): IReplStageState<HistoryItemT> {

    override val currentGeneration: Int get() = history.currentGeneration.get()

    override val history: BasicReplStageHistory<HistoryItemT> = BasicReplStageHistory(lock)
}
