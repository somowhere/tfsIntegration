/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.ui.DocumentAdapter;
/*     */ import com.intellij.ui.IdeBorderFactory;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
/*     */ import org.jetbrains.tfsIntegration.ui.servertree.ServerBrowserDialog;
/*     */ 
/*     */ public class CreateBranchForm
/*     */ {
/*     */   private JTextField mySourceField;
/*     */   private SelectRevisionForm myRevisionForm;
/*     */   private JCheckBox myCreateLocalWorkingCopiesCheckBox;
/*     */   private TextFieldWithBrowseButton.NoPathCompletion myTargetField;
/*     */   private JPanel myContentPane;
/*     */   private JLabel myTargetLabel;
/*     */   private final EventDispatcher<ChangeListener> myEventDispatcher;
/*     */   
/*     */   public CreateBranchForm(final Project project, final WorkspaceInfo workspace, String serverPath, boolean isDirectory) {
/*  45 */    this.myEventDispatcher = EventDispatcher.create(ChangeListener.class);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  51 */     this.mySourceField.setText(serverPath);
/*     */     
/*  53 */     this.myTargetLabel.setLabelFor(this.myTargetField.getChildComponent());
/*  54 */     this.myTargetField.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/*  58 */             String serverPath = (CreateBranchForm.this.myTargetField.getText() != null && CreateBranchForm.this.myTargetField.getText().length() > 0) ? CreateBranchForm.this.myTargetField.getText() : CreateBranchForm.this.mySourceField.getText();
/*     */             
/*  60 */             ServerBrowserDialog d = new ServerBrowserDialog(TFSBundle.message("choose.branch.target.folder.dialog.title", new Object[0]), project, workspace.getServer(), serverPath, true, true);
/*     */             
/*  62 */             if (d.showAndGet()) {
/*  63 */               CreateBranchForm.this.myTargetField.setText(d.getSelectedPath());
/*     */             }
/*     */           }
/*     */         });
/*     */     
/*  68 */     ((JTextField)this.myTargetField.getChildComponent()).getDocument().addDocumentListener((DocumentListener)new DocumentAdapter()
/*     */         {
/*     */           protected void textChanged(@NotNull DocumentEvent e) {
/*  71 */              ((ChangeListener)CreateBranchForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(e));
/*     */           }
/*     */         });
/*     */     
/*  75 */     this.myRevisionForm.init(project, workspace, serverPath, isDirectory);
/*     */   }
/*     */ 
/*     */   
/*  79 */   public void addListener(ChangeListener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  84 */   public VersionSpecBase getVersionSpec() { return this.myRevisionForm.getVersionSpec(); }
/*     */ 
/*     */ 
/*     */   
/*  88 */   public JComponent getContentPane() { return this.myContentPane; }
/*     */ 
/*     */ 
/*     */   
/*  92 */   public String getTargetPath() { return this.myTargetField.getText(); }
/*     */ 
/*     */ 
/*     */   
/*  96 */   public boolean isCreateWorkingCopies() { return this.myCreateLocalWorkingCopiesCheckBox.isSelected(); }
/*     */ 
/*     */ 
/*     */   
/* 100 */   public JComponent getPreferredFocusedComponent() { return this.myTargetField.getChildComponent(); }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\CreateBranchForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */