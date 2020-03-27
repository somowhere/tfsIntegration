package org.jetbrains.tfsIntegration.core.revision;

import java.io.IOException;
import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public interface TFSContentStore {
  void saveContent(TfsFileUtil.ContentWriter paramContentWriter) throws TfsException, IOException;
  
  byte[] loadContent() throws TfsException, IOException;
}


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\revision\TFSContentStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */