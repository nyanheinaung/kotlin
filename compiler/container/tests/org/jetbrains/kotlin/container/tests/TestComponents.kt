/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.container.tests

import java.io.*

interface TestComponentInterface {
    val disposed: Boolean
    fun foo()
}

interface TestClientComponentInterface {
}

class TestComponent : TestComponentInterface, Closeable {
    override var disposed: Boolean = false
    override fun close() {
        disposed = true
    }

    override fun foo() {
        throw UnsupportedOperationException()
    }
}

class ManualTestComponent(val name: String) : TestComponentInterface, Closeable {
    override var disposed: Boolean = false
    override fun close() {
        disposed = true
    }

    override fun foo() {
        throw UnsupportedOperationException()
    }
}

class TestClientComponent(val dep: TestComponentInterface) : TestClientComponentInterface, Closeable {
    override fun close() {
        if (dep.disposed)
            throw Exception("Dependency shouldn't be disposed before dependee")
        disposed = true
    }

    var disposed: Boolean = false
}

class TestClientComponent2() : TestClientComponentInterface {
}

class TestAdhocComponentService
class TestAdhocComponent1(val service: TestAdhocComponentService) {

}

class TestAdhocComponent2(val service: TestAdhocComponentService) {

}

class TestIterableComponent(val components: Iterable<TestClientComponentInterface>)

interface TestGenericComponent<T>

class TestGenericClient(val component1 : TestGenericComponent<String>, val component2: TestGenericComponent<Int>)
class TestStringComponent : TestGenericComponent<String>
class TestIntComponent : TestGenericComponent<Int>

class TestImplicitGeneric<T>()
class TestImplicitGenericClient(val component: TestImplicitGeneric<String>)
