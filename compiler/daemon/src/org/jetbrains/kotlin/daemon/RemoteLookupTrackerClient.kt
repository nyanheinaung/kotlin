/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.daemon

import com.intellij.util.containers.StringInterner
import gnu.trove.THashMap
import gnu.trove.THashSet
import org.jetbrains.kotlin.daemon.common.CompilerCallbackServicesFacade
import org.jetbrains.kotlin.daemon.common.DummyProfiler
import org.jetbrains.kotlin.daemon.common.Profiler
import org.jetbrains.kotlin.incremental.components.LookupInfo
import org.jetbrains.kotlin.incremental.components.LookupTracker
import org.jetbrains.kotlin.incremental.components.Position
import org.jetbrains.kotlin.incremental.components.ScopeKind


class RemoteLookupTrackerClient(
    val facade: CompilerCallbackServicesFacade,
    eventManager: EventManager,
    val profiler: Profiler = DummyProfiler()
) : LookupTracker {
    private val isDoNothing = profiler.withMeasure(this) { facade.lookupTracker_isDoNothing() }

    // Map: FileName -> (ScopeFqName -> Set<Name[String] | LookupInfo>)
    private val lookups = THashMap<String, MutableMap<String, MutableSet<Any>>>()
    private val interner = StringInterner()

    override val requiresPosition: Boolean = profiler.withMeasure(this) { facade.lookupTracker_requiresPosition() }

    override fun record(filePath: String, position: Position, scopeFqName: String, scopeKind: ScopeKind, name: String) {
        if (isDoNothing) return

        val internedSymbolFqName = interner.intern(scopeFqName)
        val internedName = interner.intern(name)

        val objectToPut: Any =
            if (requiresPosition)
                LookupInfo(filePath, position, scopeFqName, scopeKind, name)
            else
                internedName

        lookups.getOrPut(filePath, ::THashMap).getOrPut(internedSymbolFqName, ::THashSet).add(objectToPut)
    }

    init {
        eventManager.onCompilationFinished { flush() }
    }

    private fun flush() {
        if (isDoNothing || lookups.isEmpty) return

        profiler.withMeasure(this) {
            facade.lookupTracker_record(
                lookups.flatMap { (filePath, lookupsByFile) ->
                    lookupsByFile.flatMap { (scopeFqName, lookupsByScopeFqName) ->
                        lookupsByScopeFqName.map { lookupInfoOrString ->
                            if (requiresPosition)
                                lookupInfoOrString as LookupInfo
                            else
                                LookupInfo(
                                    filePath, Position.NO_POSITION, scopeFqName, ScopeKind.CLASSIFIER,
                                    lookupInfoOrString as String
                                )
                        }
                    }
                }
            )
        }

        lookups.clear()
    }
}
