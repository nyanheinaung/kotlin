/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.cli.common;

import org.jetbrains.kotlin.backend.common.phaser.PhaseConfig;
import org.jetbrains.kotlin.cli.common.config.ContentRoot;
import org.jetbrains.kotlin.cli.common.messages.MessageCollector;
import org.jetbrains.kotlin.config.CompilerConfigurationKey;

import java.io.File;
import java.util.List;

public class CLIConfigurationKeys {
    // Roots, including dependencies and own sources
    public static final CompilerConfigurationKey<List<ContentRoot>> CONTENT_ROOTS =
            CompilerConfigurationKey.create("content roots");

    public static final CompilerConfigurationKey<MessageCollector> MESSAGE_COLLECTOR_KEY =
            CompilerConfigurationKey.create("message collector");
    public static final CompilerConfigurationKey<Boolean> ALLOW_KOTLIN_PACKAGE =
            CompilerConfigurationKey.create("allow kotlin package");
    public static final CompilerConfigurationKey<CommonCompilerPerformanceManager> PERF_MANAGER =
            CompilerConfigurationKey.create("performance manager");

    // Used in Eclipse plugin (see KotlinCLICompiler)
    public static final CompilerConfigurationKey<String> INTELLIJ_PLUGIN_ROOT =
            CompilerConfigurationKey.create("intellij plugin root");

    // See K2MetadataCompilerArguments

    public static final CompilerConfigurationKey<File> METADATA_DESTINATION_DIRECTORY =
            CompilerConfigurationKey.create("metadata destination directory");

    public static final CompilerConfigurationKey<PhaseConfig> PHASE_CONFIG =
            CompilerConfigurationKey.create("phase configuration");

    private CLIConfigurationKeys() {
    }
}
