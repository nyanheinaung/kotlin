/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.scopes

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.utils.Printer

class LexicalScopeImpl @JvmOverloads constructor(
    parent: HierarchicalScope,
    override val ownerDescriptor: DeclarationDescriptor,
    override val isOwnerDescriptorAccessibleByLabel: Boolean,
    override val implicitReceiver: ReceiverParameterDescriptor?,
    override val kind: LexicalScopeKind,
    redeclarationChecker: LocalRedeclarationChecker = LocalRedeclarationChecker.DO_NOTHING,
    initialize: LexicalScopeImpl.InitializeHandler.() -> Unit = {}
) : LexicalScope, LexicalScopeStorage(parent, redeclarationChecker) {

    init {
        InitializeHandler().initialize()
    }

    override fun toString(): String = kind.toString()

    override fun printStructure(p: Printer) {
        p.println(
            this::class.java.simpleName, ": ", kind, "; for descriptor: ", ownerDescriptor.name,
            " with implicitReceiver: ", implicitReceiver?.value ?: "NONE", " {"
        )
        p.pushIndent()

        p.print("parent = ")
        parent.printStructure(p.withholdIndentOnce())

        p.popIndent()
        p.println("}")
    }

    inner class InitializeHandler() {

        fun addVariableDescriptor(variableDescriptor: VariableDescriptor): Unit =
            this@LexicalScopeImpl.addVariableOrClassDescriptor(variableDescriptor)

        fun addFunctionDescriptor(functionDescriptor: FunctionDescriptor): Unit =
            this@LexicalScopeImpl.addFunctionDescriptorInternal(functionDescriptor)

        fun addClassifierDescriptor(classifierDescriptor: ClassifierDescriptor): Unit =
            this@LexicalScopeImpl.addVariableOrClassDescriptor(classifierDescriptor)

    }
}
