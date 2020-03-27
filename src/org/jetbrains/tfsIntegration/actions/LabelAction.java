/*    */ package org.jetbrains.tfsIntegration.actions;
/*    */ 
/*    */ import com.intellij.openapi.project.DumbAware;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.MessageType;
/*    */ import com.intellij.openapi.util.text.StringUtil;
/*    */ import com.intellij.openapi.vcs.AbstractVcsHelper;
/*    */ import com.intellij.openapi.vcs.FilePath;
/*    */ import com.intellij.openapi.vcs.VcsException;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
/*    */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelResult;
/*    */ import java.text.MessageFormat;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import org.jetbrains.annotations.NotNull;
/*    */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.ResultWithFailures;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*    */ import org.jetbrains.tfsIntegration.ui.ApplyLabelDialog;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LabelAction
/*    */   extends SingleItemAction
/*    */   implements DumbAware
/*    */ {
/*    */   protected void execute(@NotNull Project project, @NotNull WorkspaceInfo workspace, @NotNull FilePath localPath, @NotNull ExtendedItem extendedItem) {
/* 48 */     ApplyLabelDialog d = new ApplyLabelDialog(project, workspace, extendedItem.getSitem());
/* 49 */     if (!d.showAndGet()) {
/*    */       return;
/*    */     }
/*    */     
/* 53 */     List<VcsException> errors = new ArrayList<>();
/*    */ 
/*    */     
/*    */     try {
/* 57 */       ResultWithFailures<LabelResult> resultWithFailures = workspace.getServer().getVCS().labelItem(d.getLabelName(), d.getLabelComment(), d.getLabelItemSpecs(), project, TFSBundle.message("creating.label", new Object[0]));
/*    */       
/* 59 */       errors.addAll(TfsUtil.getVcsExceptions(resultWithFailures.getFailures()));
/*    */       
/* 61 */       StringBuilder buffer = new StringBuilder();
/* 62 */       for (LabelResult labelResult : resultWithFailures.getResult()) {
/* 63 */         if (buffer.length() > 0) {
/* 64 */           buffer.append("\n");
/*    */         }
/* 66 */         String message = MessageFormat.format("Label ''{0}@{1}'' {2}", new Object[] { labelResult.getLabel(), labelResult.getScope(), 
/* 67 */               StringUtil.toLowerCase(labelResult.getStatus().getValue()) });
/* 68 */         buffer.append(message);
/*    */       } 
/* 70 */       if (buffer.length() > 0) {
/* 71 */         TfsUtil.showBalloon(project, MessageType.INFO, buffer.toString());
/*    */       }
/*    */     }
/* 74 */     catch (TfsException e) {
/* 75 */       errors.add(new VcsException((Throwable)e));
/*    */     } 
/*    */     
/* 78 */     if (!errors.isEmpty())
/* 79 */       AbstractVcsHelper.getInstance(project).showErrors(errors, "TFS: Apply Label"); 
/*    */   }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\actions\LabelAction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */