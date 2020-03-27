/*    */ package org.jetbrains.tfsIntegration.core.tfs;
/*    */ 
/*    */ import com.intellij.openapi.options.Configurable;
/*    */ import com.intellij.openapi.options.ConfigurationException;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import java.util.Map;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.tfsIntegration.core.TFSProjectConfiguration;
/*    */ import org.jetbrains.tfsIntegration.ui.UpdateSettingsForm;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class UpdateConfigurable
/*    */   implements Configurable
/*    */ {
/*    */   private final Project myProject;
/*    */   private final Map<WorkspaceInfo, UpdateSettingsForm.WorkspaceSettings> myWorkspaceSettings;
/*    */   private UpdateSettingsForm myUpdateSettingsForm;
/*    */   
/*    */   public UpdateConfigurable(Project project, Map<WorkspaceInfo, UpdateSettingsForm.WorkspaceSettings> workspaceSettings) {
/* 36 */     this.myProject = project;
/* 37 */     this.myWorkspaceSettings = workspaceSettings;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 42 */   public void apply() throws ConfigurationException { this.myUpdateSettingsForm.apply(TFSProjectConfiguration.getInstance(this.myProject)); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 47 */   public void reset() { this.myUpdateSettingsForm.reset(TFSProjectConfiguration.getInstance(this.myProject)); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nls
/* 53 */   public String getDisplayName() { return "Update Project"; }
/*    */ 
/*    */ 
/*    */   
/*    */   public JComponent createComponent() {
/* 58 */     this.myUpdateSettingsForm = new UpdateSettingsForm(this.myProject, getDisplayName(), this.myWorkspaceSettings);
/* 59 */     return this.myUpdateSettingsForm.getPanel();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 64 */   public boolean isModified() { return false; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 69 */   public void disposeUIResources() { this.myUpdateSettingsForm = null; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\UpdateConfigurable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */