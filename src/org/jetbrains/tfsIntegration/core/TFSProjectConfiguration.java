/*    */ package org.jetbrains.tfsIntegration.core;
/*    */ 
/*    */ import com.intellij.openapi.components.PersistentStateComponent;
/*    */ import com.intellij.openapi.components.ServiceManager;
/*    */ import com.intellij.openapi.components.State;
/*    */ import com.intellij.openapi.components.Storage;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.UpdateWorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @State(name = "TFS", storages = {@Storage("$WORKSPACE_FILE$")})
/*    */ public class TFSProjectConfiguration
/*    */   implements PersistentStateComponent<TFSProjectConfiguration.ConfigurationBean>
/*    */ {
/* 33 */   private final Map<WorkspaceInfo, UpdateWorkspaceInfo> myUpdateWorkspaceInfos = new HashMap<>();
/*    */ 
/*    */   
/*    */   public static class ConfigurationBean
/*    */   {
/*    */     public boolean UPDATE_RECURSIVELY = true;
/*    */   }
/* 40 */   private ConfigurationBean myConfigurationBean = new ConfigurationBean();
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 45 */   public static TFSProjectConfiguration getInstance(@NotNull Project project) {    return (TFSProjectConfiguration)ServiceManager.getService(project, TFSProjectConfiguration.class); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/* 51 */   public ConfigurationBean getState() {    return this.myConfigurationBean; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 56 */   public void loadState(@NotNull ConfigurationBean state) {    this.myConfigurationBean = state; }
/*    */ 
/*    */   
/*    */   public UpdateWorkspaceInfo getUpdateWorkspaceInfo(WorkspaceInfo workspace) {
/* 60 */     UpdateWorkspaceInfo info = this.myUpdateWorkspaceInfos.get(workspace);
/* 61 */     if (info == null) {
/* 62 */       info = new UpdateWorkspaceInfo((VersionSpecBase)LatestVersionSpec.INSTANCE);
/* 63 */       this.myUpdateWorkspaceInfos.put(workspace, info);
/*    */     } 
/* 65 */     return info;
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSProjectConfiguration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */