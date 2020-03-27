/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.DialogWrapper;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemSpec;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.RecursionType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionSpec;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.List;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlServer;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.labels.LabelItemSpecWithItems;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.ui.servertree.TfsTreeForm;
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
/*     */ public class AddItemDialog
/*     */   extends DialogWrapper
/*     */ {
/*     */   private final Project myProject;
/*     */   private final WorkspaceInfo myWorkspace;
/*     */   private final AddItemForm myForm;
/*     */   private LabelItemSpecWithItems myLabelSpec;
/*     */   
/*     */   public AddItemDialog(Project project, WorkspaceInfo workspace, String sourcePath) {
/*  48 */     super(project, true);
/*  49 */     this.myForm = new AddItemForm(project, workspace, sourcePath);
/*  50 */     this.myProject = project;
/*  51 */     this.myWorkspace = workspace;
/*     */     
/*  53 */     setTitle("Add Item");
/*     */     
/*  55 */     init();
/*     */     
/*  57 */     this.myForm.addListener(new ChangeListener()
/*     */         {
/*     */           public void stateChanged(ChangeEvent e) {
/*  60 */             AddItemDialog.this.updateButtons();
/*     */           }
/*     */         });
/*  63 */     updateButtons();
/*     */   }
/*     */ 
/*     */   
/*  67 */   private void updateButtons() { setOKActionEnabled((this.myForm.getServerItem() != null && this.myForm.getVersion() != null)); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  73 */   protected JComponent createCenterPanel() { return this.myForm.getContentPane(); }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  78 */   public LabelItemSpecWithItems getLabelSpec() { return this.myLabelSpec; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  83 */   public JComponent getPreferredFocusedComponent() { return this.myForm.getPreferredFocusedComponent(); }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void doOKAction() {
/*     */     try {
/*  89 */       TfsTreeForm.SelectedItem serverItem = this.myForm.getServerItem();
/*     */       
/*  91 */       ItemSpec itemSpec = VersionControlServer.createItemSpec(serverItem.path, serverItem.isDirectory ? RecursionType.Full : null);
/*     */       
/*  93 */       List<Item> items = this.myWorkspace.getServer().getVCS().queryItems(itemSpec, (VersionSpec)this.myForm.getVersion(), getContentPane(), TFSBundle.message("loading.item", new Object[0]));
/*  94 */       if (!items.isEmpty()) {
/*  95 */         this.myLabelSpec = LabelItemSpecWithItems.createForAdd(itemSpec, this.myForm.getVersion(), items);
/*     */       } else {
/*     */         
/*  98 */         String message = MessageFormat.format("Item ''{0}'' was not found in source control at version ''{1}''.", new Object[] { serverItem.path, this.myForm
/*  99 */               .getVersion().getPresentableString() });
/*     */         
/* 101 */         Messages.showErrorDialog(this.myProject, message, "Apply label");
/*     */         
/*     */         return;
/*     */       } 
/* 105 */     } catch (TfsException e) {
/* 106 */       Messages.showErrorDialog(this.myProject, e.getMessage(), "Apply label");
/*     */       return;
/*     */     } 
/* 109 */     super.doOKAction();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 114 */   protected String getDimensionServiceKey() { return "TFS.AddItem"; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\AddItemDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */