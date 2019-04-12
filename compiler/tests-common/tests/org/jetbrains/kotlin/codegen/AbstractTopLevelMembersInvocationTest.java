/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import com.intellij.util.ArrayUtil;
import kotlin.collections.CollectionsKt;
import kotlin.io.FilesKt;
import kotlin.sequences.SequencesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles;
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment;
import org.jetbrains.kotlin.test.CompilerTestUtil;
import org.jetbrains.kotlin.test.ConfigurationKind;
import org.jetbrains.kotlin.test.KotlinTestUtils;
import org.jetbrains.kotlin.test.TestJdkKind;

import java.io.File;
import java.util.Collections;
import java.util.List;

public abstract class AbstractTopLevelMembersInvocationTest extends AbstractBytecodeTextTest {
    @Override
    public void doTest(@NotNull String filename) throws Exception {
        File root = new File(filename);
        List<String> sourceFiles = SequencesKt.toList(SequencesKt.map(
                SequencesKt.filter(FilesKt.walkTopDown(root).maxDepth(1), File::isFile),
                this::relativePath
        ));

        File library = new File(root, "library");
        List<File> classPath =
                library.exists()
                ? Collections.singletonList(CompilerTestUtil.compileJvmLibrary(library))
                : Collections.emptyList();

        assert !sourceFiles.isEmpty() : getTestName(true) + " should contain at least one .kt file";
        Collections.sort(sourceFiles);

        myEnvironment = KotlinCoreEnvironment.createForTests(
                getTestRootDisposable(),
                KotlinTestUtils.newConfiguration(
                        ConfigurationKind.JDK_ONLY, TestJdkKind.MOCK_JDK,
                        CollectionsKt.plus(classPath, KotlinTestUtils.getAnnotationsJar()), Collections.emptyList()
                ),
                EnvironmentConfigFiles.JVM_CONFIG_FILES);

        loadFiles(ArrayUtil.toStringArray(sourceFiles));

        List<OccurrenceInfo> expected = readExpectedOccurrences(KotlinTestUtils.getTestDataPathBase() + "/codegen/" + sourceFiles.get(0));
        String actual = generateToText();
        Companion.checkGeneratedTextAgainstExpectedOccurrences(actual, expected);
    }
}
