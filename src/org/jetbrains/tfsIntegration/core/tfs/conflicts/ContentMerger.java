package org.jetbrains.tfsIntegration.core.tfs.conflicts;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.history.VcsRevisionNumber;
import com.intellij.openapi.vfs.VirtualFile;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
import java.io.IOException;
import org.jetbrains.tfsIntegration.ui.ContentTriplet;

public interface ContentMerger {
  boolean mergeContent(Conflict paramConflict, ContentTriplet paramContentTriplet, Project paramProject, VirtualFile paramVirtualFile, String paramString, VcsRevisionNumber paramVcsRevisionNumber) throws IOException, VcsException;
}


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\conflicts\ContentMerger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */