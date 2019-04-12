/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import com.intellij.openapi.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.config.CompilerConfiguration;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.test.ConfigurationKind;
import org.jetbrains.kotlin.test.TestJdkKind;
import org.jetbrains.kotlin.utils.ExceptionUtilsKt;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;

import static org.jetbrains.kotlin.script.ScriptTestUtilKt.loadScriptingPlugin;

public abstract class AbstractScriptCodegenTest extends CodegenTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        createEnvironmentWithMockJdkAndIdeaAnnotations(ConfigurationKind.JDK_ONLY);
    }

    @Override
    protected void updateConfiguration(@NotNull CompilerConfiguration configuration) {
        loadScriptingPlugin(configuration);
        super.updateConfiguration(configuration);
    }

    @Override
    protected void doTest(@NotNull String filename) {
        loadFileByFullPath(filename);

        try {
            //noinspection ConstantConditions
            FqName fqName = myFiles.getPsiFile().getScript().getFqName();
            Class<?> scriptClass = generateClass(fqName.asString());

            Constructor constructor = getTheOnlyConstructor(scriptClass);
            Object scriptInstance = constructor.newInstance(myFiles.getScriptParameterValues().toArray());

            assertFalse("expecting at least one expectation", myFiles.getExpectedValues().isEmpty());

            for (Pair<String, String> nameValue : myFiles.getExpectedValues()) {
                String fieldName = nameValue.first;
                String expectedValue = nameValue.second;

                if (expectedValue.equals("<nofield>")) {
                    try {
                        scriptClass.getDeclaredField(fieldName);
                        fail("must have no field " + fieldName);
                    }
                    catch (NoSuchFieldException e) {
                        continue;
                    }
                }

                Field field = scriptClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                Object result = field.get(scriptInstance);
                String resultString = result != null ? result.toString() : "null";
                assertEquals("comparing field " + fieldName, expectedValue, resultString);
            }
        }
        catch (Throwable e) {
            System.out.println(generateToText());
            throw ExceptionUtilsKt.rethrow(e);
        }
    }

    @NotNull
    private static Constructor getTheOnlyConstructor(@NotNull Class<?> clazz) {
        Constructor[] constructors = clazz.getConstructors();
        if (constructors.length != 1) {
            throw new IllegalArgumentException("Script class should have one constructor: " + clazz);
        }
        return constructors[0];
    }
}
