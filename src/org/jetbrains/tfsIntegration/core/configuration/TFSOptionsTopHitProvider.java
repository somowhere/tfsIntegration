/*    */ package org.jetbrains.tfsIntegration.core.configuration;
/*    */ 
/*    */ import com.intellij.ide.ui.OptionsSearchTopHitProvider;
/*    */ import com.intellij.ide.ui.PublicMethodBasedOptionDescription;
/*    */ import com.intellij.ide.ui.search.BooleanOptionDescription;
/*    */ import com.intellij.ide.ui.search.OptionDescription;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vcs.ProjectLevelVcsManager;
/*    */ import com.intellij.openapi.vcs.impl.VcsDescriptor;
/*    */ import java.lang.reflect.Field;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collection;
/*    */ import java.util.Collections;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class TFSOptionsTopHitProvider
/*    */   implements OptionsSearchTopHitProvider.ProjectLevelProvider
/*    */ {
/*    */   @NotNull
/* 24 */   public String getId() {     return "vcs"; }
/*    */ 
/*    */ 
/*    */   
/*    */   @NotNull
/*    */   public Collection<OptionDescription> getOptions(@NotNull Project project) {
/* 30 */        for (VcsDescriptor descriptor : ProjectLevelVcsManager.getInstance(project).getAllVcss()) {
/* 31 */       if ("TFS".equals(descriptor.getDisplayName())) {
/* 32 */          return Collections.unmodifiableCollection((Collection)Arrays.asList((Object[])new BooleanOptionDescription[] { (BooleanOptionDescription)new Option("TFS: Use HTTP Proxy settings", null, "useIdeaHttpProxy", "setUseIdeaHttpProxy"), (BooleanOptionDescription)new Option("TFS: Evaluate Team Explorer policies", "teamExplorer", null, "setSupportTfsCheckinPolicies"), (BooleanOptionDescription)new Option("TFS: Evaluate Teamprise policies", "teamprise", null, "setSupportStatefulCheckinPolicies"), (BooleanOptionDescription)new Option("TFS: Warn about not installed policies", "nonInstalled", null, "setReportNotInstalledCheckinPolicies") }));
/*    */       } 
/*    */     } 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 39 */          return Collections.emptyList();
/*    */   }
/*    */   
/*    */   private static final class Option extends PublicMethodBasedOptionDescription {
/*    */     private final String myField;
/*    */     
/*    */     Option(String option, String field, String getterName, String setterName) {
/* 46 */       super(option, "vcs.TFS", getterName, setterName);
/* 47 */       this.myField = field;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/* 52 */     public TFSConfigurationManager getInstance() { return TFSConfigurationManager.getInstance(); }
/*    */ 
/*    */ 
/*    */     
/*    */     public boolean isOptionEnabled() {
/* 57 */       if (this.myField == null) {
/* 58 */         return super.isOptionEnabled();
/*    */       }
/*    */       try {
/* 61 */         Object instance = getInstance().getCheckinPoliciesCompatibility();
/* 62 */         Field field = instance.getClass().getField(this.myField);
/* 63 */         return field.getBoolean(instance);
/*    */       }
/* 65 */       catch (NoSuchFieldException noSuchFieldException) {
/*    */       
/* 67 */       } catch (IllegalAccessException illegalAccessException) {}
/*    */       
/* 69 */       return false;
/*    */     }
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\configuration\TFSOptionsTopHitProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */