/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package example

import java.lang.annotation.Inherited

@Inherited
annotation class ExampleAnnotation

@Inherited
@Retention(AnnotationRetention.SOURCE)
annotation class ExampleSourceAnnotation

@Inherited
@Retention(AnnotationRetention.BINARY)
annotation class ExampleBinaryAnnotation

@Inherited
@Retention(AnnotationRetention.RUNTIME)
annotation class ExampleRuntimeAnnotation

annotation class GenError