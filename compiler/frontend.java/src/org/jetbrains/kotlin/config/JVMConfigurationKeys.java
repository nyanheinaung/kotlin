/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.config;

import org.jetbrains.kotlin.load.java.JavaClassesTracker;
import org.jetbrains.kotlin.load.kotlin.incremental.components.IncrementalCompilationComponents;
import org.jetbrains.kotlin.modules.Module;
import org.jetbrains.kotlin.script.KotlinScriptDefinition;
import org.jetbrains.kotlin.script.ScriptDefinitionsSource;

import java.io.File;
import java.util.List;

public class JVMConfigurationKeys {
    private JVMConfigurationKeys() {
    }

    public static final CompilerConfigurationKey<File> OUTPUT_DIRECTORY =
            CompilerConfigurationKey.create("output directory");
    public static final CompilerConfigurationKey<File> OUTPUT_JAR =
            CompilerConfigurationKey.create("output .jar");
    public static final CompilerConfigurationKey<Boolean> INCLUDE_RUNTIME =
            CompilerConfigurationKey.create("include runtime to the resulting .jar");

    public static final CompilerConfigurationKey<File> JDK_HOME =
            CompilerConfigurationKey.create("jdk home");

    public static final CompilerConfigurationKey<Boolean> NO_JDK =
            CompilerConfigurationKey.create("no jdk");

    public static final CompilerConfigurationKey<List<KotlinScriptDefinition>> SCRIPT_DEFINITIONS =
            CompilerConfigurationKey.create("script definitions");

    public static final CompilerConfigurationKey<List<ScriptDefinitionsSource>> SCRIPT_DEFINITIONS_SOURCES =
            CompilerConfigurationKey.create("script definitions sources");

    public static final CompilerConfigurationKey<Boolean> DISABLE_STANDARD_SCRIPT_DEFINITION =
            CompilerConfigurationKey.create("Disable standard kotlin script support");

    public static final CompilerConfigurationKey<Boolean> RETAIN_OUTPUT_IN_MEMORY =
            CompilerConfigurationKey.create("retain compiled classes in memory for further use, e.g. when running scripts");

    public static final CompilerConfigurationKey<Boolean> DISABLE_CALL_ASSERTIONS =
            CompilerConfigurationKey.create("disable not-null call assertions");
    public static final CompilerConfigurationKey<Boolean> DISABLE_RECEIVER_ASSERTIONS =
            CompilerConfigurationKey.create("disable not-null call receiver assertions");
    public static final CompilerConfigurationKey<Boolean> DISABLE_PARAM_ASSERTIONS =
            CompilerConfigurationKey.create("disable not-null parameter assertions");
    public static final CompilerConfigurationKey<JVMAssertionsMode> ASSERTIONS_MODE =
            CompilerConfigurationKey.create("assertions mode");
    public static final CompilerConfigurationKey<JVMConstructorCallNormalizationMode> CONSTRUCTOR_CALL_NORMALIZATION_MODE =
            CompilerConfigurationKey.create("constructor call normalization mode");
    public static final CompilerConfigurationKey<Boolean> NO_EXCEPTION_ON_EXPLICIT_EQUALS_FOR_BOXED_NULL =
            CompilerConfigurationKey.create("do not throw NPE on explicit 'equals' call for null receiver of platform boxed primitive type");
    public static final CompilerConfigurationKey<Boolean> DISABLE_OPTIMIZATION =
            CompilerConfigurationKey.create("disable optimization");
    public static final CompilerConfigurationKey<Boolean> USE_TYPE_TABLE =
            CompilerConfigurationKey.create("use type table in serializer");

    public static final CompilerConfigurationKey<Boolean> USE_SINGLE_MODULE =
            CompilerConfigurationKey.create("combine modules for source files and binary dependencies into a single module");

    public static final CompilerConfigurationKey<Boolean> SKIP_RUNTIME_VERSION_CHECK =
            CompilerConfigurationKey.create("do not perform checks on runtime versions consistency");

    public static final CompilerConfigurationKey<JvmTarget> JVM_TARGET =
            CompilerConfigurationKey.create("JVM bytecode target version");

    public static final CompilerConfigurationKey<Boolean> PARAMETERS_METADATA =
            CompilerConfigurationKey.create("Parameters metadata for java 1.8 reflection");
    
    public static final CompilerConfigurationKey<IncrementalCompilationComponents> INCREMENTAL_COMPILATION_COMPONENTS =
            CompilerConfigurationKey.create("incremental cache provider");

    public static final CompilerConfigurationKey<JavaClassesTracker> JAVA_CLASSES_TRACKER =
            CompilerConfigurationKey.create("Java classes tracker");

    public static final CompilerConfigurationKey<File> MODULE_XML_FILE =
            CompilerConfigurationKey.create("path to module.xml");

    public static final CompilerConfigurationKey<String> DECLARATIONS_JSON_PATH =
            CompilerConfigurationKey.create("path to declarations output");

    public static final CompilerConfigurationKey<List<Module>> MODULES =
            CompilerConfigurationKey.create("module data");

    public static final CompilerConfigurationKey<List<String>> FRIEND_PATHS =
            CompilerConfigurationKey.create("friend module paths");

    public static final CompilerConfigurationKey<Boolean> IR =
            CompilerConfigurationKey.create("IR");

    public static final CompilerConfigurationKey<Boolean> USE_FAST_CLASS_FILES_READING =
            CompilerConfigurationKey.create("use fast class files reading implementation [experimental]");

    public static final CompilerConfigurationKey<Boolean> USE_JAVAC =
            CompilerConfigurationKey.create("use javac [experimental]");

    public static final CompilerConfigurationKey<Boolean> COMPILE_JAVA =
            CompilerConfigurationKey.create("compile java files [experimental]");

    public static final CompilerConfigurationKey<List<String>> ADDITIONAL_JAVA_MODULES =
            CompilerConfigurationKey.create("additional Java modules");

    public static final CompilerConfigurationKey<Boolean> ENABLE_JVM_DEFAULT =
            CompilerConfigurationKey.create("Allow to use '@JvmDefault'");
}
