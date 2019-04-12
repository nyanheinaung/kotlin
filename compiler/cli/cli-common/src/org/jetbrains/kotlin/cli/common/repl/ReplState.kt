/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common.repl

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


interface ILineId : Comparable<ILineId> {
    val no: Int
    val generation: Int
}

data class ReplHistoryRecord<out T> (val id: ILineId, val item: T)

interface IReplStageHistory<T> : List<ReplHistoryRecord<T>> {

    fun peek(): ReplHistoryRecord<T>? = lock.read { lastOrNull() }

    fun push(id: ILineId, item: T)

    fun pop(): ReplHistoryRecord<T>?

    fun verifiedPop(id: ILineId): ReplHistoryRecord<T>? = lock.write {
        if (lastOrNull()?.id == id) pop()
        else null
    }

    fun reset(): Iterable<ILineId>

    fun resetTo(id: ILineId): Iterable<ILineId>

    val lock: ReentrantReadWriteLock
}

interface IReplStageState<T> {
    val history: IReplStageHistory<T>

    val lock: ReentrantReadWriteLock

    val currentGeneration: Int

    fun getNextLineNo(): Int = history.peek()?.id?.no?.let { it + 1 } ?: REPL_CODE_LINE_FIRST_NO // TODO: it should be more robust downstream (e.g. use atomic)

    fun <StateT : IReplStageState<*>> asState(target: Class<out StateT>): StateT =
            if (target.isAssignableFrom(this::class.java)) this as StateT
            else throw IllegalArgumentException("$this is not an expected instance of IReplStageState")
}


fun <T> IReplStageHistory<T>.firstMismatch(other: Sequence<ILineId>): Pair<ReplHistoryRecord<T>?, ILineId?>? =
        lock.read {
            iterator().asSequence().zip(other.asSequence()).firstOrNull { it.first.id != it.second }?.let { it.first to it.second }
        }

fun<T> IReplStageHistory<T>.firstMismatchFiltered(other: Sequence<ILineId>, predicate: (ReplHistoryRecord<T>) -> Boolean): Pair<ReplHistoryRecord<T>?, ILineId?>? =
        lock.read {
            iterator().asSequence().filter(predicate).zip(other.asSequence()).firstOrNull { it.first.id != it.second }?.let { it.first to it.second }
        }

fun<T> IReplStageHistory<T>.firstMismatchWhile(other: Sequence<ILineId>, predicate: (ReplHistoryRecord<T>) -> Boolean): Pair<ReplHistoryRecord<T>?, ILineId?>? =
        lock.read {
            iterator().asSequence().takeWhile(predicate).zip(other.asSequence()).firstOrNull { it.first.id != it.second }?.let { it.first to it.second }
        }

