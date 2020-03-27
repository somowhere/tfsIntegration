//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.actions.VcsContextFactory.SERVICE;
import com.intellij.openapi.vcs.changes.VcsDirtyScopeManager;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.RefreshQueue;
import com.intellij.ui.GuiUtils;
import com.intellij.util.io.DigestUtil;
import com.intellij.util.io.ReadOnlyAttributeUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.TFSVcs;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public class TfsFileUtil {
    public TfsFileUtil() {
    }

    public static List<FilePath> getFilePaths(@NotNull VirtualFile[] files) {

        return getFilePaths((Collection)Arrays.asList(files));
    }

    public static List<FilePath> getFilePaths(@NotNull Collection<? extends VirtualFile> files) {

        List<FilePath> paths = new ArrayList(files.size());
        Iterator var2 = files.iterator();

        while(var2.hasNext()) {
            VirtualFile f = (VirtualFile)var2.next();
            paths.add(getFilePath(f));
        }

        return paths;
    }

    public static FilePath getFilePath(@NotNull VirtualFile f) {

        return SERVICE.getInstance().createFilePathOn(f);
    }

    public static void setReadOnly(VirtualFile file, boolean status) throws IOException {
        setReadOnly((Collection)Collections.singletonList(file), status);
    }

    public static void setReadOnly(Collection<? extends VirtualFile> files, boolean status) throws IOException {
        Ref exception = new Ref();

        try {
            GuiUtils.runOrInvokeAndWait(() -> {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        Iterator var3 = files.iterator();

                        while(var3.hasNext()) {
                            VirtualFile file = (VirtualFile)var3.next();
                            ReadOnlyAttributeUtil.setReadOnlyAttribute(file, status);
                        }
                    } catch (IOException var5) {
                        exception.set(var5);
                    }

                });
            });
        } catch (InvocationTargetException var4) {
        } catch (InterruptedException var5) {
        }

        if (!exception.isNull()) {
            throw (IOException)exception.get();
        }
    }

    private static void setReadOnly(String path, boolean status) throws IOException {
        Ref exception = new Ref();

        try {
            GuiUtils.runOrInvokeAndWait(() -> {
                try {
                    ReadOnlyAttributeUtil.setReadOnlyAttribute(path, status);
                } catch (IOException var4) {
                    exception.set(var4);
                }

            });
        } catch (InvocationTargetException var4) {
        } catch (InterruptedException var5) {
        }

        if (!exception.isNull()) {
            throw (IOException)exception.get();
        }
    }

    public static void markFileDirty(Project project, @NotNull FilePath file) {

        ApplicationManager.getApplication().runReadAction(() -> {
            VcsDirtyScopeManager.getInstance(project).fileDirty(file);
        });
    }

    public static void markDirtyRecursively(Project project, Collection<? extends FilePath> roots) {
        if (!roots.isEmpty()) {
            ApplicationManager.getApplication().runReadAction(() -> {
                Iterator var2 = roots.iterator();

                while(var2.hasNext()) {
                    FilePath root = (FilePath)var2.next();
                    VcsDirtyScopeManager.getInstance(project).dirDirtyRecursively(root);
                }

            });
        }
    }

    public static void markDirty(Project project, Collection<? extends FilePath> roots, Collection<? extends FilePath> files) {
        if (!roots.isEmpty() || !files.isEmpty()) {
            ApplicationManager.getApplication().runReadAction(() -> {
                Iterator var3 = roots.iterator();

                FilePath file;
                while(var3.hasNext()) {
                    file = (FilePath)var3.next();
                    VcsDirtyScopeManager.getInstance(project).dirDirtyRecursively(file);
                }

                var3 = files.iterator();

                while(var3.hasNext()) {
                    file = (FilePath)var3.next();
                    VcsDirtyScopeManager.getInstance(project).fileDirty(file);
                }

            });
        }
    }

    public static void markDirtyRecursively(Project project, FilePath rootDir) {
        ApplicationManager.getApplication().runReadAction(() -> {
            VcsDirtyScopeManager.getInstance(project).dirDirtyRecursively(rootDir);
        });
    }

    public static void markFileDirty(Project project, @NotNull VirtualFile file) {
        ApplicationManager.getApplication().runReadAction(() -> {
            VcsDirtyScopeManager.getInstance(project).fileDirty(file);
        });
    }

    public static void refreshAndMarkDirty(Project project, Collection<? extends VirtualFile> roots, boolean async) {
        refreshAndMarkDirty(project, VfsUtilCore.toVirtualFileArray(roots), async);
    }

    public static void refreshAndInvalidate(Project project, FilePath[] roots, boolean async) {
        VirtualFile[] files = new VirtualFile[roots.length];

        for(int i = 0; i < roots.length; ++i) {
            files[i] = roots[i].getVirtualFile();
        }

        refreshAndMarkDirty(project, files, async);
    }

    public static void refreshAndMarkDirty(Project project, VirtualFile[] roots, boolean async) {
        RefreshQueue.getInstance().refresh(async, true, () -> {
            VirtualFile[] var2 = roots;
            int var3 = roots.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                VirtualFile root = var2[var4];

                try {
                    TFSVcs.assertTrue(root != null);
                    VcsDirtyScopeManager.getInstance(project).dirDirtyRecursively(root);
                } catch (RuntimeException var7) {
                    TFSVcs.error("Error in refresh delegate: " + var7);
                }
            }

        }, roots);
    }

    public static void refreshAndFindFile(FilePath path) {
        try {
            GuiUtils.runOrInvokeAndWait(() -> {
                ApplicationManager.getApplication().runWriteAction(() -> {
                    VirtualFileManager.getInstance().refreshAndFindFileByUrl(path.getPath());
                });
            });
        } catch (InvocationTargetException var2) {
        } catch (InterruptedException var3) {
        }

    }

    public static void setFileContent(@NotNull File destination, @NotNull TfsFileUtil.ContentWriter contentWriter) throws TfsException, IOException {

        TFSVcs.assertTrue(!destination.isDirectory(), destination + " expected to be a file");
        FileOutputStream fileStream = null;

        try {
            if (destination.exists() && !destination.canWrite()) {
                setReadOnly(destination.getPath(), false);
            }

            fileStream = new FileOutputStream(destination);
            contentWriter.write(fileStream);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException var9) {
                }
            }

        }

    }

    public static boolean hasWritableChildFile(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            File[] var2 = files;
            int var3 = files.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                File child = var2[var4];
                if (child.isFile() && child.canWrite() || hasWritableChildFile(child)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isFileWritable(FilePath localPath) {
        VirtualFile file = localPath.getVirtualFile();
        return file.isWritable() && !file.isDirectory();
    }

    public static boolean localItemExists(FilePath localPath) {
        VirtualFile file = localPath.getVirtualFile();
        return file != null && file.isValid() && file.exists();
    }

    public static byte[] calculateMD5(File file) throws IOException {
        MessageDigest digest = DigestUtil.md5();
        BufferedInputStream is = null;

        try {
            is = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[8192];

            int read;
            while((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }

            byte[] var5 = digest.digest();
            return var5;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException var12) {
                }
            }

        }
    }

    public interface ContentWriter {
        void write(OutputStream var1) throws TfsException;
    }
}
