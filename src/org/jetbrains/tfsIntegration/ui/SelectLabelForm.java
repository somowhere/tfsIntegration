/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.ui.DoubleClickListener;
/*     */ import com.intellij.ui.IdeBorderFactory;
/*     */ import com.intellij.ui.TitledSeparator;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.table.JBTable;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionControlLabel;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.util.Collections;
/*     */ import java.util.EventListener;
/*     */ import java.util.List;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SelectLabelForm
/*     */ {
/*     */   private JTextField myNameField;
/*     */   private JTextField myOwnerField;
/*     */   private JButton myFindButton;
/*     */   private JTable myLabelsTable;
/*     */   private JPanel myContentPane;
/*     */   private final LabelsTableModel myLabelsTableModel;
/*     */   private final EventDispatcher<Listener> myEventDispatcher;
/*     */   
/*     */   public SelectLabelForm(final SelectLabelDialog dialog, final WorkspaceInfo workspace) {
/*  55 */         this.myEventDispatcher = EventDispatcher.create(Listener.class);
/*     */ 
/*     */     
/*  58 */     this.myLabelsTableModel = new LabelsTableModel();
/*  59 */     this.myLabelsTable.setModel(this.myLabelsTableModel);
/*  60 */     this.myLabelsTable.setSelectionMode(0);
/*     */     
/*  62 */     this.myLabelsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*     */         {
/*     */           public void valueChanged(ListSelectionEvent e) {
/*  65 */             ((SelectLabelForm.Listener)SelectLabelForm.this.myEventDispatcher.getMulticaster()).selectionChanged();
/*     */           }
/*     */         });
/*     */     
/*  69 */     (new DoubleClickListener()
/*     */       {
/*     */         protected boolean onDoubleClick(MouseEvent e) {
/*  72 */           if (SelectLabelForm.this.isLabelSelected()) {
/*  73 */             dialog.close(0);
/*  74 */             return true;
/*     */           } 
/*  76 */           return false;
/*     */         }
/*  78 */       }).installOn(this.myLabelsTable);
/*     */     
/*  80 */     this.myFindButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/*     */             try {
/*  84 */               String owner = SelectLabelForm.this.myOwnerField.getText().trim();
/*  85 */               if ("".equals(owner)) {
/*  86 */                 owner = null;
/*     */               }
/*  88 */               String name = SelectLabelForm.this.myNameField.getText().trim();
/*  89 */               if ("".equals(name)) {
/*  90 */                 name = null;
/*     */               }
/*     */ 
/*     */ 
/*     */               
/*  95 */               List<VersionControlLabel> labels = workspace.getServer().getVCS().queryLabels(name, "$/", owner, false, null, null, false, SelectLabelForm.this.getContentPane(), 
/*  96 */                   TFSBundle.message("searching.for.label", new Object[0]));
/*  97 */               SelectLabelForm.this.myLabelsTableModel.setLabels(labels);
/*     */             }
/*  99 */             catch (TfsException ex) {
/* 100 */               SelectLabelForm.this.myLabelsTableModel.setLabels(Collections.emptyList());
/* 101 */               Messages.showErrorDialog(SelectLabelForm.this.myContentPane, ex.getMessage(), "Find Label");
/*     */             } finally {
/*     */               
/* 104 */               ((SelectLabelForm.Listener)SelectLabelForm.this.myEventDispatcher.getMulticaster()).selectionChanged();
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/* 111 */   public JPanel getContentPane() { return this.myContentPane; }
/*     */ 
/*     */ 
/*     */   
/* 115 */   public void addListener(Listener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 119 */   public void removeListener(Listener listener) { this.myEventDispatcher.removeListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 123 */   public boolean isLabelSelected() { return (this.myLabelsTable.getSelectedRowCount() == 1); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/* 128 */   public VersionControlLabel getLabel() {      return this.myLabelsTableModel.getLabels().get(this.myLabelsTable.getSelectedRow()); }
/*     */   
/*     */   public static interface Listener extends EventListener {
/*     */     void selectionChanged();
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\SelectLabelForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */