/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.DialogWrapper;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LockLevel;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.util.List;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.Action;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.locks.LockItemModel;
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
/*     */ public class LockItemsDialog
/*     */   extends DialogWrapper
/*     */ {
/*     */   public static final int LOCK_EXIT_CODE = 2;
/*     */   public static final int UNLOCK_EXIT_CODE = 3;
/*     */   private final LockItemsForm myLockItemsForm;
/*     */   private final Action myLockAction;
/*     */   private final Action myUnlockAction;
/*     */   
/*     */   public LockItemsDialog(Project project, List<LockItemModel> items) {
/*  41 */     super(project, false);
/*     */     
/*  43 */     this.myLockItemsForm = new LockItemsForm(items);
/*     */     
/*  45 */     setTitle("Lock/Unlock");
/*     */     
/*  47 */     this.myLockAction = new LockAction();
/*  48 */     this.myUnlockAction = new UnlockAction();
/*     */     
/*  50 */     init();
/*     */     
/*  52 */     this.myLockItemsForm.addListener(new LockItemsTableModel.Listener()
/*     */         {
/*     */           public void selectionChanged() {
/*  55 */             LockItemsDialog.this.updateControls();
/*     */           }
/*     */         });
/*  58 */     updateControls();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  64 */   protected JComponent createCenterPanel() { return this.myLockItemsForm.getContentPane(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*  70 */   protected Action[] createActions() { (new Action[3])[0] = getLockAction(); (new Action[3])[1] = getUnlockAction(); (new Action[3])[2] = getCancelAction();  return new Action[3]; }
/*     */ 
/*     */ 
/*     */   
/*  74 */   private Action getLockAction() { return this.myLockAction; }
/*     */ 
/*     */ 
/*     */   
/*  78 */   private Action getUnlockAction() { return this.myUnlockAction; }
/*     */ 
/*     */   
/*     */   private void setLockActionEnabled(boolean isEnabled) {
/*  82 */     this.myLockAction.setEnabled(isEnabled);
/*  83 */     this.myLockItemsForm.setRadioButtonsEnabled(isEnabled);
/*     */   }
/*     */ 
/*     */   
/*  87 */   private void setUnlockActionEnabled(boolean isEnabled) { this.myUnlockAction.setEnabled(isEnabled); }
/*     */ 
/*     */   
/*     */   private void updateControls() {
/*  91 */     List<LockItemModel> items = getSelectedItems();
/*  92 */     setLockActionEnabled((!items.isEmpty() && canAllBeLocked(items)));
/*  93 */     setUnlockActionEnabled((!items.isEmpty() && canAllBeUnlocked(items)));
/*     */   }
/*     */   
/*     */   private static boolean canAllBeLocked(List<LockItemModel> items) {
/*  97 */     for (LockItemModel item : items) {
/*  98 */       if (!item.canBeLocked()) {
/*  99 */         return false;
/*     */       }
/*     */     } 
/* 102 */     return true;
/*     */   }
/*     */   
/*     */   private static boolean canAllBeUnlocked(List<LockItemModel> items) {
/* 106 */     for (LockItemModel item : items) {
/* 107 */       if (!item.canBeUnlocked()) {
/* 108 */         return false;
/*     */       }
/*     */     } 
/* 111 */     return true;
/*     */   }
/*     */ 
/*     */   
/* 115 */   public List<LockItemModel> getSelectedItems() { return this.myLockItemsForm.getSelectedItems(); }
/*     */ 
/*     */ 
/*     */   
/* 119 */   public LockLevel getLockLevel() { return this.myLockItemsForm.getLockLevel(); }
/*     */   
/*     */   private class LockAction
/*     */     extends AbstractAction {
/*     */     LockAction() {
/* 124 */       super("Lock");
/* 125 */       putValue("DefaultAction", Boolean.TRUE);
/*     */     }
/*     */ 
/*     */     
/*     */     public void actionPerformed(ActionEvent e) {
/* 130 */       if (LockItemsDialog.this.myPerformAction)
/*     */         return;  try {
/* 132 */         LockItemsDialog.this.myPerformAction = true;
/* 133 */         if (LockItemsDialog.this.getLockAction().isEnabled()) {
/* 134 */           LockItemsDialog.this.close(2);
/*     */         }
/*     */       } finally {
/*     */         
/* 138 */         LockItemsDialog.this.myPerformAction = false;
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private class UnlockAction
/*     */     extends AbstractAction {
/* 145 */     UnlockAction() { super("Unlock"); }
/*     */ 
/*     */ 
/*     */     
/*     */     public void actionPerformed(ActionEvent e) {
/* 150 */       if (LockItemsDialog.this.myPerformAction)
/*     */         return;  try {
/* 152 */         LockItemsDialog.this.myPerformAction = true;
/* 153 */         if (LockItemsDialog.this.getUnlockAction().isEnabled()) {
/* 154 */           LockItemsDialog.this.close(3);
/*     */         }
/*     */       } finally {
/*     */         
/* 158 */         LockItemsDialog.this.myPerformAction = false;
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 165 */   protected String getDimensionServiceKey() { return "TFS.LockItems"; }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\LockItemsDialog.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */