/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.ui.DialogWrapper;
/*    */ import com.intellij.openapi.util.Disposer;
/*    */ import javax.swing.JComponent;
/*    */ import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
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
/*    */ public class CheckinParametersDialog
/*    */   extends DialogWrapper
/*    */ {
/*    */   private final CheckinParameters myParameters;
/*    */   private CheckinParametersForm myForm;
/*    */   private final Project myProject;
/*    */   
/*    */   public CheckinParametersDialog(Project project, CheckinParameters parameters) {
/* 32 */     super(project, true);
/* 33 */     this.myProject = project;
/* 34 */     this.myParameters = parameters;
/* 35 */     setTitle("Configure Checkin Parameters");
/* 36 */     init();
/*    */     
/* 38 */     setSize(700, 500);
/*    */   }
/*    */ 
/*    */   
/*    */   protected JComponent createCenterPanel() {
/* 43 */     this.myForm = new CheckinParametersForm(this.myParameters, this.myProject);
/* 44 */     Disposer.register(getDisposable(), this.myForm);
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 49 */     return this.myForm.getContentPane();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/* 54 */   protected String getDimensionServiceKey() { return "TFS.CheckIn.Parameters"; }
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\CheckinParametersDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */