/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.test;

import junit.framework.Test;
import junit.framework.TestResult;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.*;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Runner that is responsible for executing tests including test methods from all inner classes.
 * Works differently for Gradle and JPS. Default is Gradle for now.
 */
public class JUnit3RunnerWithInners extends Runner implements Filterable, Sortable {

    static {
        KotlinTestUtils.setIdeaSystemPathProperties();
    }

    private final Runner delegateRunner;

    public JUnit3RunnerWithInners(Class<?> klass) {
        super();

        if ("true".equals(System.getProperty("use.jps"))) {
            delegateRunner = new JUnit3RunnerWithInnersForJPS(klass);
        }
        else {
            delegateRunner = new JUnit3RunnerWithInnersForGradle(klass);
        }
    }

    @Override
    public void run(RunNotifier notifier) {
        delegateRunner.run(notifier);
    }

    @Override
    public Description getDescription() {
        return delegateRunner.getDescription();
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        ((Filterable)delegateRunner).filter(filter);
    }

    @Override
    public void sort(Sorter sorter) {
        ((Sortable)delegateRunner).sort(sorter);
    }

    static class FakeEmptyClassTest implements Test, Filterable {
        private final String className;

        FakeEmptyClassTest(Class<?> klass) {
            this.className = klass.getName();
        }

        @Override
        public int countTestCases() {
            return 0;
        }

        @Override
        public void run(TestResult result) {
            result.startTest(this);
            result.endTest(this);
        }

        @Override
        public String toString() {
            return "Empty class with inners for " + className;
        }

        @Override
        public void filter(Filter filter) throws NoTestsRemainException {
            throw new NoTestsRemainException();
        }
    }

    static boolean isTestMethod(Method method) {
        return method.getParameterTypes().length == 0 &&
               method.getName().startsWith("test") &&
               method.getReturnType().equals(Void.TYPE) &&
               Modifier.isPublic(method.getModifiers());
    }
}