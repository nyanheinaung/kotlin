/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.modules.xml;

import com.intellij.openapi.util.io.FileUtil;
import junit.framework.TestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation;
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity;
import org.jetbrains.kotlin.cli.common.messages.MessageCollector;
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer;
import org.jetbrains.kotlin.cli.common.modules.ModuleChunk;
import org.jetbrains.kotlin.cli.common.modules.ModuleXmlParser;
import org.jetbrains.kotlin.modules.Module;
import org.jetbrains.kotlin.test.KotlinTestUtils;

import java.io.File;
import java.io.IOException;

public abstract class AbstractModuleXmlParserTest extends TestCase {

    @SuppressWarnings("MethodMayBeStatic")
    protected void doTest(String xmlPath) throws IOException {
        File txtFile = new File(FileUtil.getNameWithoutExtension(xmlPath) + ".txt");

        ModuleChunk result = ModuleXmlParser.parseModuleScript(xmlPath, new MessageCollector() {
            @Override
            public void report(
                    @NotNull CompilerMessageSeverity severity, @NotNull String message, @Nullable CompilerMessageLocation location
            ) {
                throw new AssertionError(MessageRenderer.PLAIN_FULL_PATHS.render(severity, message, location));
            }

            @Override
            public void clear() {
                // Do nothing
            }

            @Override
            public boolean hasErrors() {
                throw new UnsupportedOperationException();
            }
        });

        StringBuilder sb = new StringBuilder();
        for (Module module : result.getModules()) {
            sb.append(moduleToString(module)).append("\n");
        }

        String actual = sb.toString();

        if (!txtFile.exists()) {
            FileUtil.writeToFile(txtFile, actual);
            fail("Expected data file does not exist. A new file created: " + txtFile);
        }

        KotlinTestUtils.assertEqualsToFile(txtFile, actual);
    }

    private static String moduleToString(@NotNull Module module) {
        return module.getModuleName() +
               "\n\ttype=" + module.getModuleType() +
               "\n\toutputDir=" + module.getOutputDirectory() +
               "\n\tsources=" + module.getSourceFiles() +
               "\n\tclasspath=" + module.getClasspathRoots();
    }
}
