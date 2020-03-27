/*    */ package org.jetbrains.tfsIntegration.core;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.vcs.AbstractVcs;
/*    */ import com.intellij.openapi.vcs.actions.StandardVcsGroup;
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
/*    */ public class TFSGroup
/*    */   extends StandardVcsGroup
/*    */ {
/* 27 */   public AbstractVcs getVcs(Project project) { return TFSVcs.getInstance(project); }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 32 */   public String getVcsName(Project project) { return "TFS"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSGroup.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */