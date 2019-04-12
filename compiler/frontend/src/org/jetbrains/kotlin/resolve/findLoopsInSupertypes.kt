/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:JvmName("FindLoopsInSupertypes")

package org.jetbrains.kotlin.resolve

import org.jetbrains.kotlin.descriptors.SupertypeLoopChecker
import org.jetbrains.kotlin.resolve.descriptorUtil.isCompanionObject
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeConstructor
import org.jetbrains.kotlin.utils.DFS
import org.jetbrains.kotlin.utils.SmartList

class SupertypeLoopCheckerImpl : SupertypeLoopChecker {
    override fun findLoopsInSupertypesAndDisconnect(
        currentTypeConstructor: TypeConstructor,
        superTypes: Collection<KotlinType>,
        neighbors: (TypeConstructor) -> Iterable<KotlinType>,
        reportLoop: (KotlinType) -> Unit
    ): Collection<KotlinType> {
        val graph = DFS.Neighbors<TypeConstructor> { node -> neighbors(node).map { it.constructor } }

        val superTypesToRemove = SmartList<KotlinType>()

        for (superType in superTypes) {
            if (isReachable(superType.constructor, currentTypeConstructor, graph)) {
                superTypesToRemove.add(superType)
                reportLoop(superType)

                currentTypeConstructor.declarationDescriptor?.let {
                    if (it.isCompanionObject()) {
                        reportLoop(it.defaultType)
                    }
                }
            }
        }

        return if (superTypesToRemove.isEmpty()) superTypes else superTypes - superTypesToRemove
    }
}

private fun isReachable(
    from: TypeConstructor, to: TypeConstructor,
    neighbors: DFS.Neighbors<TypeConstructor>
): Boolean {
    var result = false
    DFS.dfs(listOf(from), neighbors, DFS.VisitedWithSet(), object : DFS.AbstractNodeHandler<TypeConstructor, Unit>() {
        override fun beforeChildren(current: TypeConstructor): Boolean {
            if (current == to) {
                result = true
                return false
            }
            return true
        }

        override fun result() = Unit
    })

    return result
}
