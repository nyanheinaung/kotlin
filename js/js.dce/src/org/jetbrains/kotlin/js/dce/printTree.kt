/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.dce

import org.jetbrains.kotlin.js.dce.Context.Node

fun printTree(root: Node, consumer: (String) -> Unit, printNestedMembers: Boolean = false, showLocations: Boolean = false) {
    printTree(root, consumer, 0, Settings(printNestedMembers, showLocations))
}

private fun printTree(node: Node, consumer: (String) -> Unit, depth: Int, settings: Settings) {
    val sb = StringBuilder()
    sb.append("  ".repeat(depth)).append(node.qualifier?.memberName ?: node.toString())

    if (node.reachable) {
        sb.append(" (reachable")
        if (settings.showLocations) {
            val locations = node.usedByAstNodes.mapNotNull { it.extractLocation() }
            if (locations.isNotEmpty()) {
                sb.append(" from ").append(locations.joinToString { it.asString() })
            }
        }
        sb.append(")")
    }

    consumer(sb.toString())

    for (memberName in node.memberNames.sorted()) {
        val member = node.member(memberName)
        if (!member.declarationReachable) continue

        if ((!node.reachable || !member.reachable) || settings.printNestedMembers) {
            printTree(member, consumer, depth + 1, settings)
        }
    }
}

private class Settings(val printNestedMembers: Boolean, val showLocations: Boolean)