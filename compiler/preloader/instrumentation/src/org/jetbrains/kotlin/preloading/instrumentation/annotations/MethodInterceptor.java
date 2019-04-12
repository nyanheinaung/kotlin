/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.preloading.instrumentation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodInterceptor {
    // JVM internal name like java/util/Map$Entry or short name like FooBar
    String className();

    // regexp, if omitted, field name is used
    String methodName() default "";

    // regexp for method descriptor, like (ILjava/lang/Object;)V for void foo(int, Object)
    String methodDesc() default "";

    // if this is false, an exception is thrown when more than one method in the same class matches
    boolean allowMultipleMatches() default false;

    // if true, every method instrumented with this interceptor will be logged to stdout
    boolean logInterceptions() default false;

    // if true, byte codes of every method instrumented with this interceptor will be logged to stdout
    boolean dumpByteCode() default false;
}
