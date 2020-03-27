/*    */ package org.jetbrains.tfsIntegration.core;
/*    */ 
/*    */ import com.intellij.openapi.options.Configurable;
/*    */ import com.intellij.openapi.options.ConfigurationException;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.annotations.Nls;
/*    */ import org.jetbrains.annotations.NonNls;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
/*    */ import org.jetbrains.tfsIntegration.core.configuration.TfsCheckinPoliciesCompatibility;
/*    */ import org.jetbrains.tfsIntegration.ui.ProjectConfigurableForm;
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
/*    */ 
/*    */ public class TFSProjectConfigurable
/*    */   implements Configurable
/*    */ {
/*    */   private final Project myProject;
/*    */   private ProjectConfigurableForm myComponent;
/*    */   
/* 37 */   public TFSProjectConfigurable(Project project) { this.myProject = project; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/*    */   @Nls
/* 44 */   public String getDisplayName() { return null; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @NonNls
/* 50 */   public String getHelpTopic() { return "project.propVCSSupport.VCSs.TFS"; }
/*    */ 
/*    */ 
/*    */   
/*    */   public JComponent createComponent() {
/* 55 */     this.myComponent = new ProjectConfigurableForm(this.myProject);
/* 56 */     return this.myComponent.getContentPane();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isModified() {
/* 61 */     if (TFSConfigurationManager.getInstance().useIdeaHttpProxy() != this.myComponent.useProxy()) return true; 
/* 62 */     TfsCheckinPoliciesCompatibility c = TFSConfigurationManager.getInstance().getCheckinPoliciesCompatibility();
/* 63 */     if (c.teamExplorer != this.myComponent.supportTfsCheckinPolicies()) return true; 
/* 64 */     if (c.teamprise != this.myComponent.supportStatefulCheckinPolicies()) return true; 
/* 65 */     if (c.nonInstalled != this.myComponent.reportNotInstalledCheckinPolicies()) return true; 
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public void apply() throws ConfigurationException {
/* 71 */     TFSConfigurationManager.getInstance().setUseIdeaHttpProxy(this.myComponent.useProxy());
/* 72 */     TFSConfigurationManager.getInstance().setSupportTfsCheckinPolicies(this.myComponent.supportTfsCheckinPolicies());
/* 73 */     TFSConfigurationManager.getInstance().setSupportStatefulCheckinPolicies(this.myComponent.supportStatefulCheckinPolicies());
/* 74 */     TFSConfigurationManager.getInstance().setReportNotInstalledCheckinPolicies(this.myComponent.reportNotInstalledCheckinPolicies());
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 79 */     this.myComponent.setUserProxy(TFSConfigurationManager.getInstance().useIdeaHttpProxy());
/* 80 */     TfsCheckinPoliciesCompatibility c = TFSConfigurationManager.getInstance().getCheckinPoliciesCompatibility();
/* 81 */     this.myComponent.setSupportTfsCheckinPolicies(c.teamExplorer);
/* 82 */     this.myComponent.setSupportStatefulCheckinPolicies(c.teamprise);
/* 83 */     this.myComponent.setReportNotInstalledCheckinPolicies(c.nonInstalled);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 88 */   public void disposeUIResources() { this.myComponent = null; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSProjectConfigurable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */