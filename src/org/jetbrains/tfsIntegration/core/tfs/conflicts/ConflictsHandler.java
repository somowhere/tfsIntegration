package org.jetbrains.tfsIntegration.core.tfs.conflicts;

import org.jetbrains.tfsIntegration.exceptions.TfsException;

public interface ConflictsHandler {
  void resolveConflicts(ResolveConflictHelper paramResolveConflictHelper) throws TfsException;
}


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\conflicts\ConflictsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */