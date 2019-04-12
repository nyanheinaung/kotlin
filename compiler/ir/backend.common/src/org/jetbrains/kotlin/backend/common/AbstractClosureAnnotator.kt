/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrLocalDelegatedProperty
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.resolve.DescriptorUtils
import java.util.*

class Closure(val capturedValues: List<ValueDescriptor>)

abstract class AbstractClosureAnnotator : IrElementVisitorVoid {
    protected abstract fun recordFunctionClosure(functionDescriptor: FunctionDescriptor, closure: Closure)
    protected abstract fun recordClassClosure(classDescriptor: ClassDescriptor, closure: Closure)

    private class ClosureBuilder(val owner: DeclarationDescriptor) {
        val capturedValues = mutableSetOf<ValueDescriptor>()

        fun buildClosure() = Closure(capturedValues.toList())

        fun addNested(closure: Closure) {
            fillInNestedClosure(capturedValues, closure.capturedValues)
        }

        private fun <T : CallableDescriptor> fillInNestedClosure(destination: MutableSet<T>, nested: List<T>) {
            nested.filterTo(destination) {
                it.containingDeclaration != owner
            }
        }
    }

    private val closuresStack = ArrayDeque<ClosureBuilder>()

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitClass(declaration: IrClass) {
        val classDescriptor = declaration.descriptor
        val closureBuilder = ClosureBuilder(classDescriptor)

        closuresStack.push(closureBuilder)
        declaration.acceptChildrenVoid(this)
        closuresStack.pop()

        val closure = closureBuilder.buildClosure()

        if (DescriptorUtils.isLocal(classDescriptor)) {
            recordClassClosure(classDescriptor, closure)
        }

        closuresStack.peek()?.addNested(closure)
    }

    override fun visitFunction(declaration: IrFunction) {
        val functionDescriptor = declaration.descriptor
        val closureBuilder = ClosureBuilder(functionDescriptor)

        closuresStack.push(closureBuilder)
        declaration.acceptChildrenVoid(this)
        closuresStack.pop()

        val closure = closureBuilder.buildClosure()

        if (DescriptorUtils.isLocal(functionDescriptor)) {
            recordFunctionClosure(functionDescriptor, closure)
        }

        closuresStack.peek()?.addNested(closure)
    }

    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty) {
        // Getter and setter of local delegated properties are special generated functions and don't have closure.
        declaration.delegate.initializer?.acceptVoid(this)
    }

    override fun visitVariableAccess(expression: IrValueAccessExpression) {
        val closureBuilder = closuresStack.peek() ?: return

        val variableDescriptor = expression.descriptor
        if (variableDescriptor.containingDeclaration != closureBuilder.owner) {
            closureBuilder.capturedValues.add(variableDescriptor)
        }

        expression.acceptChildrenVoid(this)
    }

}
