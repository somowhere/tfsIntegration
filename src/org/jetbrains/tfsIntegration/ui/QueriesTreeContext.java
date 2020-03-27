package org.jetbrains.tfsIntegration.ui;

import com.microsoft.tfs.core.TFSTeamProjectCollection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil;

public interface QueriesTreeContext {
  @NotNull
  CheckinParameters getState();
  
  @NotNull
  ServerInfo getServer();
  
  @NotNull
  TFSTeamProjectCollection getProjectCollection();
  
  void queryWorkItems(@NotNull TfsExecutionUtil.Process<WorkItemsQueryResult> paramProcess);
}


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\QueriesTreeContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */