/*    */ package org.jetbrains.tfsIntegration.ui;
/*    */ 
/*    */ import com.intellij.openapi.Disposable;
/*    */ import com.intellij.openapi.project.Project;
/*    */ import com.intellij.openapi.util.Disposer;
/*    */ import com.intellij.ui.IdeBorderFactory;
/*    */ import com.intellij.ui.TitledSeparator;
/*    */ import com.intellij.uiDesigner.core.GridConstraints;
/*    */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*    */ import com.intellij.uiDesigner.core.Spacer;
/*    */ import com.intellij.util.EventDispatcher;
/*    */ import java.awt.Component;
/*    */ import java.awt.Insets;
/*    */ import java.awt.LayoutManager;
/*    */ import javax.swing.BorderFactory;
/*    */ import javax.swing.JComponent;
/*    */ import javax.swing.JPanel;
/*    */ import javax.swing.event.ChangeEvent;
/*    */ import javax.swing.event.ChangeListener;
/*    */ import org.jetbrains.annotations.Nullable;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*    */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
/*    */ import org.jetbrains.tfsIntegration.ui.servertree.TfsTreeForm;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AddItemForm
/*    */   implements Disposable
/*    */ {
/*    */   private JPanel myContentPane;
/*    */   private SelectRevisionForm mySelectRevisionForm;
/*    */   private TfsTreeForm myTreeForm;
/*    */   private TitledSeparator myServerItemSeparator;
/*    */   private final EventDispatcher<ChangeListener> myEventDispatcher;
/*    */   
/*    */   public AddItemForm(final Project project, final WorkspaceInfo workspace, String serverPath) {
/* 40 */     this.myEventDispatcher = EventDispatcher.create(ChangeListener.class);
/*    */ 
/*    */     
/* 43 */     this.myServerItemSeparator.setLabelFor(this.myTreeForm.getPreferredFocusedComponent());
/* 44 */     Disposer.register(this, (Disposable)this.myTreeForm);
/* 45 */     this.myTreeForm.addListener(new TfsTreeForm.SelectionListener()
/*    */         {
/*    */           public void selectionChanged() {
/* 48 */             TfsTreeForm.SelectedItem selectedItem = AddItemForm.this.myTreeForm.getSelectedItem();
/* 49 */             if (selectedItem != null) {
/* 50 */               AddItemForm.this.mySelectRevisionForm.init(project, workspace, selectedItem.path, selectedItem.isDirectory);
/*    */             }
/* 52 */             ((ChangeListener)AddItemForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*    */           }
/*    */         });
/*    */     
/* 56 */     this.mySelectRevisionForm.addListener(new SelectRevisionForm.Listener()
/*    */         {
/*    */           public void revisionChanged() {
/* 59 */             ((ChangeListener)AddItemForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*    */           }
/*    */         });
/*    */     
/* 63 */     this.myTreeForm.initialize(workspace.getServer(), serverPath, false, false, null);
/*    */   }
/*    */ 
/*    */   
/* 67 */   public JPanel getContentPane() { return this.myContentPane; }
/*    */ 
/*    */ 
/*    */   
/* 71 */   public void addListener(ChangeListener listener) { this.myEventDispatcher.addListener(listener); }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 76 */   public TfsTreeForm.SelectedItem getServerItem() { return this.myTreeForm.getSelectedItem(); }
/*    */ 
/*    */ 
/*    */   
/*    */   @Nullable
/* 81 */   public VersionSpecBase getVersion() { return this.mySelectRevisionForm.getVersionSpec(); }
/*    */ 
/*    */ 
/*    */   
/* 85 */   public JComponent getPreferredFocusedComponent() { return this.myTreeForm.getPreferredFocusedComponent(); }
/*    */   
/*    */   public void dispose() {}
/*    */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\AddItemForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */