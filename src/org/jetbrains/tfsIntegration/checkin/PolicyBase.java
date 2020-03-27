/*     */ package org.jetbrains.tfsIntegration.checkin;
/*     */ 
/*     */ import com.intellij.openapi.extensions.ExtensionPointName;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import java.text.MessageFormat;
/*     */ import org.jdom.Element;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class PolicyBase
/*     */ {
/*  33 */   public static final ExtensionPointName<PolicyBase> EP_NAME = new ExtensionPointName("TFS.checkinPolicy");
/*     */   
/*  35 */   protected static final PolicyFailure[] NO_FAILURES = new PolicyFailure[0];
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public abstract PolicyType getPolicyType();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract PolicyFailure[] evaluate(@NotNull PolicyContext paramPolicyContext, @NotNull ProgressIndicator paramProgressIndicator);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean canEdit();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract boolean edit(Project paramProject);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void loadState(@NotNull Element paramElement);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public abstract void saveState(@NotNull Element paramElement);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void activate(@NotNull Project project, @NotNull PolicyFailure policyFailure) {
/*     */     String message;
/*  98 */       if (policyFailure.getTooltipText() != null) {
/*  99 */       message = MessageFormat.format("{0}\n\n{1}", new Object[] { policyFailure.getMessage(), policyFailure.getTooltipText() });
/*     */     } else {
/*     */       
/* 102 */       message = policyFailure.getMessage();
/*     */     } 
/* 104 */     Messages.showWarningDialog(project, message, "Checkin Policy Warning");
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\PolicyBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */