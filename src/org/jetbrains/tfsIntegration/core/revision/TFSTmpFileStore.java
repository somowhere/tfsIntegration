//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.revision;

import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.StreamUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil.ContentWriter;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public class TFSTmpFileStore implements TFSContentStore {
    @NonNls
    private static final String TMP_FILE_NAME = "idea_tfs";
    private static String myTfsTmpDir;
    private final File myTmpFile;
    static File tmpDir = new File(System.getProperty("user.home")+"\\idea_tfs");

    private static void refreshTmpDir(){
        if(!tmpDir.exists()){
            tmpDir.mkdir();
        }
    }
    @Nullable
    public static TFSContentStore find(String serverUri, int itemId, int revision) throws IOException {
        refreshTmpDir();
        File tmpFile = new File(createTmpFileName(serverUri, itemId, revision));
        return tmpFile.exists() ? new TFSTmpFileStore(tmpFile) : null;
    }

    TFSTmpFileStore(String serverUri, int itemId, int revision) throws IOException {
        refreshTmpDir();
        this.myTmpFile = new File(createTmpFileName(serverUri, itemId, revision));
        this.myTmpFile.deleteOnExit();
    }

    private static String createTmpFileName(String serverUri, int itemId, int revision) throws IOException {
        return getTfsTmpDir() + File.separator + serverUri.hashCode() + "_" + itemId + "." + revision;
    }

    TFSTmpFileStore(File tmpFile) {

        refreshTmpDir();
        this.myTmpFile = tmpFile;
    }

    private static String getTfsTmpDir() throws IOException {
//        if (myTfsTmpDir == null) {
            tmpDir.delete();
            tmpDir.mkdir();
            myTfsTmpDir = tmpDir.getAbsolutePath();
//        }

        return myTfsTmpDir;
    }

    public void saveContent(ContentWriter contentWriter) throws TfsException, IOException {

        refreshTmpDir();
        TfsFileUtil.setFileContent(this.myTmpFile, contentWriter);
    }

    public byte[] loadContent() throws IOException {

        refreshTmpDir();
        FileInputStream fileStream = null;

        byte[] var2;
        try {
            fileStream = new FileInputStream(this.myTmpFile);
            var2 = StreamUtil.loadFromStream(fileStream);
        } finally {
            if (fileStream != null) {
                fileStream.close();
            }

        }

        return var2;
    }
}
