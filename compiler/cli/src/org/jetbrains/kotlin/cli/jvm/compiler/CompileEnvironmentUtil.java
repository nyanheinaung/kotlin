/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.jvm.compiler;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import kotlin.io.FilesKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.backend.common.output.OutputFile;
import org.jetbrains.kotlin.backend.common.output.OutputFileCollection;
import org.jetbrains.kotlin.cli.common.messages.MessageCollector;
import org.jetbrains.kotlin.cli.common.modules.ModuleChunk;
import org.jetbrains.kotlin.cli.common.modules.ModuleXmlParser;
import org.jetbrains.kotlin.name.FqName;
import org.jetbrains.kotlin.utils.ExceptionUtilsKt;
import org.jetbrains.kotlin.utils.PathUtil;

import java.io.*;
import java.util.jar.*;

import static org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR;

public class CompileEnvironmentUtil {
    @NotNull
    public static ModuleChunk loadModuleChunk(File buildFile, MessageCollector messageCollector) {
        if (!buildFile.exists()) {
            messageCollector.report(ERROR, "Module definition file does not exist: " + buildFile, null);
            return ModuleChunk.EMPTY;
        }
        if ("xml".equalsIgnoreCase(FilesKt.getExtension(buildFile))) {
            return ModuleXmlParser.parseModuleScript(buildFile.getPath(), messageCollector);
        }
        messageCollector.report(ERROR, "Unknown module definition type: " + buildFile, null);
        return ModuleChunk.EMPTY;
    }

    // TODO: includeRuntime should be not a flag but a path to runtime
    private static void doWriteToJar(
            OutputFileCollection outputFiles, OutputStream fos, @Nullable FqName mainClass, boolean includeRuntime
    ) {
        try {
            Manifest manifest = new Manifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            mainAttributes.putValue("Manifest-Version", "1.0");
            mainAttributes.putValue("Created-By", "JetBrains Kotlin");
            if (mainClass != null) {
                mainAttributes.putValue("Main-Class", mainClass.asString());
            }
            JarOutputStream stream = new JarOutputStream(fos, manifest);
            for (OutputFile outputFile : outputFiles.asList()) {
                stream.putNextEntry(new JarEntry(outputFile.getRelativePath()));
                stream.write(outputFile.asByteArray());
            }
            if (includeRuntime) {
                writeRuntimeToJar(stream);
            }
            stream.finish();
        }
        catch (IOException e) {
            throw new CompileEnvironmentException("Failed to generate jar file", e);
        }
    }

    public static void writeToJar(File jarPath, boolean jarRuntime, FqName mainClass, OutputFileCollection outputFiles) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(jarPath);
            doWriteToJar(outputFiles, outputStream, mainClass, jarRuntime);
            outputStream.close();
        }
        catch (FileNotFoundException e) {
            throw new CompileEnvironmentException("Invalid jar path " + jarPath, e);
        }
        catch (IOException e) {
            throw ExceptionUtilsKt.rethrow(e);
        }
        finally {
            ExceptionUtilsKt.closeQuietly(outputStream);
        }
    }

    private static void writeRuntimeToJar(JarOutputStream stream) throws IOException {
        File stdlibPath = PathUtil.getKotlinPathsForCompiler().getStdlibPath();
        if (!stdlibPath.exists()) {
            throw new CompileEnvironmentException("Couldn't find kotlin-stdlib at " + stdlibPath);
        }
        copyJarImpl(stream, stdlibPath);
    }

    private static void copyJarImpl(JarOutputStream stream, File jarPath) throws IOException {
        try (JarInputStream jis = new JarInputStream(new FileInputStream(jarPath))) {
            while (true) {
                JarEntry e = jis.getNextJarEntry();
                if (e == null) {
                    break;
                }
                if (FileUtilRt.extensionEquals(e.getName(), "class")) {
                    stream.putNextEntry(e);
                    FileUtil.copy(jis, stream);
                }
            }
        }
    }
}
