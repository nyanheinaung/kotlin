/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen

import com.google.common.collect.Lists
import com.intellij.openapi.util.Trinity
import gnu.trove.TObjectIntHashMap
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.org.objectweb.asm.Type
import java.util.*

open class FrameMap : FrameMapBase<DeclarationDescriptor>()

open class FrameMapBase<T : Any> {
    private val myVarIndex = TObjectIntHashMap<T>()
    private val myVarSizes = TObjectIntHashMap<T>()
    var currentSize = 0
        private set

    fun enter(descriptor: T, type: Type): Int {
        val index = currentSize
        myVarIndex.put(descriptor, index)
        currentSize += type.size
        myVarSizes.put(descriptor, type.size)
        return index
    }

    fun leave(descriptor: T): Int {
        val size = myVarSizes.get(descriptor)
        currentSize -= size
        myVarSizes.remove(descriptor)
        val oldIndex = myVarIndex.remove(descriptor)
        if (oldIndex != currentSize) {
            throw IllegalStateException("Descriptor can be left only if it is last: $descriptor")
        }
        return oldIndex
    }

    fun enterTemp(type: Type): Int {
        val result = currentSize
        currentSize += type.size
        return result
    }

    fun leaveTemp(type: Type) {
        currentSize -= type.size
    }

    open fun getIndex(descriptor: T): Int {
        return if (myVarIndex.contains(descriptor)) myVarIndex.get(descriptor) else -1
    }

    fun mark(): Mark {
        return Mark(currentSize)
    }

    inner class Mark(private val myIndex: Int) {

        fun dropTo() {
            val descriptorsToDrop = ArrayList<T>()
            val iterator = myVarIndex.iterator()
            while (iterator.hasNext()) {
                iterator.advance()
                if (iterator.value() >= myIndex) {
                    descriptorsToDrop.add(iterator.key())
                }
            }
            for (declarationDescriptor in descriptorsToDrop) {
                myVarIndex.remove(declarationDescriptor)
                myVarSizes.remove(declarationDescriptor)
            }
            currentSize = myIndex
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()

        if (myVarIndex.size() != myVarSizes.size()) {
            return "inconsistent"
        }

        val descriptors = Lists.newArrayList<Trinity<T, Int, Int>>()

        for (descriptor0 in myVarIndex.keys()) {
            val descriptor = descriptor0 as T
            val varIndex = myVarIndex.get(descriptor)
            val varSize = myVarSizes.get(descriptor)
            descriptors.add(Trinity.create(descriptor, varIndex, varSize))
        }

        descriptors.sortBy { left -> left.second }

        sb.append("size=").append(currentSize)

        var first = true
        for (t in descriptors) {
            if (!first) {
                sb.append(", ")
            }
            first = false
            sb.append(t.first).append(",i=").append(t.second).append(",s=").append(t.third)
        }

        return sb.toString()
    }
}
