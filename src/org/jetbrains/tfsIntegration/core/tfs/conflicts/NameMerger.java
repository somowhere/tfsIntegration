package org.jetbrains.tfsIntegration.core.tfs.conflicts;

import com.intellij.openapi.project.Project;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;

public interface NameMerger {
  @Nullable
  String mergeName(WorkspaceInfo paramWorkspaceInfo, Conflict paramConflict, Project paramProject);
}


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\conflicts\NameMerger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */