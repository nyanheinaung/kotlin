/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Project contributors. Use of this source code is governed
 * by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PathMacros;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.updateSettings.impl.UpdateChecker;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.searches.IndexPatternSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.idea.reporter.KotlinReportSubmitter;
import org.jetbrains.kotlin.idea.search.ideaExtensions.KotlinTodoSearcher;
import org.jetbrains.kotlin.utils.PathUtil;

import java.io.File;
import java.io.IOException;

import static org.jetbrains.kotlin.idea.TestResourceBundleKt.registerAdditionalResourceBundleInTests;

public class PluginStartupComponent implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance(PluginStartupComponent.class);

    private static final String KOTLIN_BUNDLED = "KOTLIN_BUNDLED";

    public static PluginStartupComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(PluginStartupComponent.class);
    }

    @Override
    @NotNull
    public String getComponentName() {
        return PluginStartupComponent.class.getName();
    }

    @Override
    public void initComponent() {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            registerAdditionalResourceBundleInTests();
        }

        registerPathVariable();

        try {
            // API added in 15.0.2
            UpdateChecker.INSTANCE.getExcludedFromUpdateCheckPlugins().add("org.jetbrains.kotlin");
        }
        catch (Throwable throwable) {
            LOG.debug("Excluding Kotlin plugin updates using old API", throwable);
            UpdateChecker.getDisabledToUpdatePlugins().add("org.jetbrains.kotlin");
        }
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent e) {
                VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(e.getDocument());
                if (virtualFile != null && virtualFile.getFileType() == KotlinFileType.INSTANCE) {
                    KotlinPluginUpdater.Companion.getInstance().kotlinFileEdited(virtualFile);
                }
            }
        });

        ServiceManager.getService(IndexPatternSearch.class).registerExecutor(new KotlinTodoSearcher());

        KotlinPluginCompatibilityVerifier.checkCompatibility();

        KotlinReportSubmitter.Companion.setupReportingFromRelease();

        //todo[Sedunov]: wait for fix in platform to avoid misunderstood from Java newbies (also ConfigureKotlinInTempDirTest)
        //KotlinSdkType.Companion.setUpIfNeeded();
    }

    private static void registerPathVariable() {
        PathMacros macros = PathMacros.getInstance();
        macros.setMacro(KOTLIN_BUNDLED, PathUtil.getKotlinPathsForIdeaPlugin().getHomePath().getPath());
    }

    private String aliveFlagPath;

    public synchronized String getAliveFlagPath() {
        if (this.aliveFlagPath == null) {
            try {
                File flagFile = File.createTempFile("kotlin-idea-", "-is-running");
                flagFile.deleteOnExit();
                this.aliveFlagPath = flagFile.getAbsolutePath();
            }
            catch (IOException e) {
                this.aliveFlagPath = "";
            }
        }
        return this.aliveFlagPath;
    }

    public synchronized void resetAliveFlag() {
        if (this.aliveFlagPath != null) {
            File flagFile = new File(this.aliveFlagPath);
            if (flagFile.exists()) {
                if (flagFile.delete()) {
                    this.aliveFlagPath = null;
                }
            }
        }
    }

    @Override
    public void disposeComponent() {}
}
