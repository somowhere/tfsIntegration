/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.wm.IdeFocusManager;
/*     */ import com.intellij.ui.DocumentAdapter;
/*     */ import com.intellij.ui.DoubleClickListener;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.table.TableView;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import com.intellij.util.ui.ListTableModel;
/*     */ import java.awt.Component;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.EventListener;
/*     */ import java.util.List;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.checkin.PolicyFailure;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class OverridePolicyWarningsForm
/*     */ {
/*     */   private JLabel myIconLabel;
/*     */   private TableView<PolicyFailure> myWarningsTable;
/*     */   private JCheckBox myOverrideCheckBox;
/*     */   private JTextArea myReasonTextArea;
/*     */   private JPanel myContentPane;
/*     */   private final EventDispatcher<Listener> myEventDispatcher;
/*     */   
/*     */   public OverridePolicyWarningsForm(final Project project, List<PolicyFailure> failures) {
/*  52 */     this.myEventDispatcher = EventDispatcher.create(Listener.class);
/*     */ 
/*     */     
/*  55 */     this.myIconLabel.setIcon(Messages.getWarningIcon());
/*     */     
/*  57 */     this.myWarningsTable.setTableHeader(null);
/*  58 */     (new DoubleClickListener()
/*     */       {
/*     */         protected boolean onDoubleClick(MouseEvent e) {
/*  61 */           PolicyFailure failure = (PolicyFailure)OverridePolicyWarningsForm.this.myWarningsTable.getSelectedObject();
/*  62 */           if (failure != null) {
/*  63 */             failure.activate(project);
/*  64 */             return true;
/*     */           } 
/*  66 */           return false;
/*     */         }
/*  68 */       }).installOn((Component)this.myWarningsTable);
/*     */     
/*  70 */     this.myWarningsTable.setModelAndUpdateColumns(new ListTableModel(new ColumnInfo[] { CheckinParametersForm.WARNING_COLUMN_INFO }, failures, -1));
/*     */ 
/*     */     
/*  73 */     this.myOverrideCheckBox.addChangeListener(new ChangeListener()
/*     */         {
/*     */           public void stateChanged(ChangeEvent e) {
/*  76 */             OverridePolicyWarningsForm.this.myReasonTextArea.setEnabled(OverridePolicyWarningsForm.this.myOverrideCheckBox.isSelected());
/*  77 */             if (OverridePolicyWarningsForm.this.myReasonTextArea.isEnabled()) {
/*  78 */               IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> IdeFocusManager.getGlobalInstance().requestFocus(OverridePolicyWarningsForm.this.myReasonTextArea, true));
/*     */             }
/*  80 */             ((OverridePolicyWarningsForm.Listener)OverridePolicyWarningsForm.this.myEventDispatcher.getMulticaster()).stateChanged();
/*     */           }
/*     */         });
/*     */     
/*  84 */     this.myReasonTextArea.getDocument().addDocumentListener((DocumentListener)new DocumentAdapter()
/*     */         {
/*     */           protected void textChanged(@NotNull DocumentEvent e) {
/*  87 */              ((OverridePolicyWarningsForm.Listener)OverridePolicyWarningsForm.this.myEventDispatcher.getMulticaster()).stateChanged();
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*  94 */   public String getReason() { return this.myOverrideCheckBox.isSelected() ? this.myReasonTextArea.getText() : null; }
/*     */ 
/*     */ 
/*     */   
/*  98 */   public void addListener(Listener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 102 */   public JComponent getContentPane() { return this.myContentPane; }
/*     */   
/*     */   public static interface Listener extends EventListener {
/*     */     void stateChanged();
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\OverridePolicyWarningsForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */