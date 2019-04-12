/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.container

import kotlin.reflect.KClass

/*Use to assist injection to provide a default implementation for a certain component and reduce boilerplate in injector code.
* Argument class must be a non-abstract component class or a kotlin object implementing target interface.
* Avoid using when there is no clear 'default' behaviour for a component.
* */
annotation class DefaultImplementation(val impl: KClass<*>)