package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.openapi.vcs.FilePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.exceptions.TfsException;

public interface StatusVisitor {
  void unversioned(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus) throws TfsException;
  
  void deleted(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus);
  
  void checkedOutForEdit(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus) throws TfsException;
  
  void scheduledForAddition(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus);
  
  void scheduledForDeletion(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus);
  
  void outOfDate(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus) throws TfsException;
  
  void upToDate(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus) throws TfsException;
  
  void renamed(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus) throws TfsException;
  
  void renamedCheckedOut(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus) throws TfsException;
  
  void undeleted(@NotNull FilePath paramFilePath, boolean paramBoolean, @NotNull ServerStatus paramServerStatus) throws TfsException;
}


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\StatusVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */