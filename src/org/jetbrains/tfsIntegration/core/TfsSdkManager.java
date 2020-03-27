//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.application.PluginPathManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.ObjectUtils;
import com.microsoft.tfs.core.config.persistence.DefaultPersistenceStoreProvider;
import com.microsoft.tfs.core.httpclient.Credentials;
import com.microsoft.tfs.core.httpclient.DefaultNTCredentials;
import com.microsoft.tfs.core.httpclient.UsernamePasswordCredentials;
import java.io.File;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;

public class TfsSdkManager {
    private TfsSdkManager() {
        setupNativeLibrariesPath();
    }

    @NotNull
    public Path getCacheFile() {
        Path var10000 = DefaultPersistenceStoreProvider.INSTANCE.getCachePersistenceStore().getStoreFile().toPath().resolve("VersionControl.config");
        return var10000;
    }

    @NotNull
    public Credentials getCredentials(@NotNull ServerInfo server) {

        org.jetbrains.tfsIntegration.core.configuration.Credentials credentials = (org.jetbrains.tfsIntegration.core.configuration.Credentials)ObjectUtils.assertNotNull(TFSConfigurationManager.getInstance().getCredentials(server.getUri()));
        Object result;
        switch(credentials.getType()) {
            case NtlmNative:
                result = new DefaultNTCredentials();
                break;
            case NtlmExplicit:
            case Alternate:
                result = new UsernamePasswordCredentials(credentials.getQualifiedUsername(), credentials.getPassword());
                break;
            default:
                throw new IllegalArgumentException("Unknown credentials type " + credentials.getType());
        }

        return (Credentials)result;
    }

    @NotNull
    public static TfsSdkManager getInstance() {
        TfsSdkManager var10000 = (TfsSdkManager)ServiceManager.getService(TfsSdkManager.class);

        return var10000;
    }

    public static void activate() {
        getInstance();
    }

    private static void setupNativeLibrariesPath() {
        File nativeLibrariesPath = new File(getPluginDirectory(), FileUtil.toSystemDependentName("lib/native"));
        System.setProperty("com.microsoft.tfs.jni.native.base-directory", nativeLibrariesPath.getPath());
    }

    @NotNull
    private static File getPluginDirectory() {
        PluginId pluginId = PluginId.getId("TFS");
        IdeaPluginDescriptor pluginDescriptor = (IdeaPluginDescriptor)ObjectUtils.assertNotNull(PluginManager.getPlugin(pluginId));
        File var10000 = pluginDescriptor.isBundled() ? PluginPathManager.getPluginHome("tfsIntegration") : (File)ObjectUtils.assertNotNull(pluginDescriptor.getPath());

        return var10000;
    }
}
