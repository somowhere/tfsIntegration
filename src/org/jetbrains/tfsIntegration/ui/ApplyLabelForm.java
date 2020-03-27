/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.ui.DocumentAdapter;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.table.JBTable;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ItemType;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LabelItemSpec;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.util.ArrayList;
/*     */ import java.util.EventListener;
/*     */ import java.util.List;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.JTextArea;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.labels.LabelItemSpecWithItems;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.labels.LabelModel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ApplyLabelForm
/*     */ {
/*     */   private final Project myProject;
/*     */   private final WorkspaceInfo myWorkspace;
/*     */   private final String mySourcePath;
/*     */   private JPanel myContentPane;
/*     */   private JTextField myLabelNameTextField;
/*     */   private JTextArea myLabelCommentTextArea;
/*     */   private JTable myTable;
/*     */   private JButton myAddButton;
/*     */   private JButton myRemoveButton;
/*     */   private LabelItemsTableModel myTableModel;
/*     */   private final LabelModel myLabelModel;
/*     */   private final EventDispatcher<Listener> myEventDispatcher;
/*     */   
/*     */   public ApplyLabelForm(Project project, WorkspaceInfo workspace, String sourcePath) {
/*  64 */     this.myEventDispatcher = EventDispatcher.create(Listener.class);
/*     */ 
/*     */     
/*  67 */     this.myProject = project;
/*  68 */     this.myWorkspace = workspace;
/*  69 */     this.mySourcePath = sourcePath;
/*  70 */     this.myLabelModel = new LabelModel();
/*     */     
/*  72 */     this.myLabelNameTextField.getDocument().addDocumentListener((DocumentListener)new DocumentAdapter()
/*     */         {
/*     */           protected void textChanged(@NotNull DocumentEvent e) {
/*  75 */               ((ApplyLabelForm.Listener)ApplyLabelForm.this.myEventDispatcher.getMulticaster()).dataChanged(ApplyLabelForm.this.getLabelName(), ApplyLabelForm.this.myTableModel.getRowCount());
/*     */           }
/*     */         });
/*     */     
/*  79 */     this.myAddButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/*  82 */             ApplyLabelForm.this.addItems();
/*     */           }
/*     */         });
/*     */     
/*  86 */     this.myRemoveButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/*  89 */             ApplyLabelForm.this.removeItems();
/*     */           }
/*     */         });
/*     */     
/*  93 */     initTable();
/*  94 */     updateButtons();
/*     */   }
/*     */   
/*     */   private void initTable() {
/*  98 */     this.myTableModel = new LabelItemsTableModel();
/*  99 */     this.myTable.setModel(this.myTableModel);
/* 100 */     for (int i = 0; i < (LabelItemsTableModel.Column.values()).length; i++) {
/* 101 */       this.myTable.getColumnModel().getColumn(i).setPreferredWidth(LabelItemsTableModel.Column.values()[i].getWidth());
/*     */     }
/* 103 */     this.myTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*     */         {
/*     */           public void valueChanged(ListSelectionEvent e) {
/* 106 */             ApplyLabelForm.this.updateButtons();
/*     */           }
/*     */         });
/*     */     
/* 110 */     this.myTable.getColumnModel().getColumn(LabelItemsTableModel.Column.Item.ordinal()).setCellRenderer(new DefaultTableCellRenderer()
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*     */           public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*     */           {
/* 118 */             Item item = (Item)value;
/* 119 */             super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/* 120 */             setIcon((item.getType() == ItemType.Folder) ? UiConstants.ICON_FOLDER : UiConstants.ICON_FILE);
/* 121 */             setValue(item.getItem());
/* 122 */             return this;
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeItems() {
/* 129 */     List<LabelItemSpecWithItems> removalSpecs = new ArrayList<>((this.myTable.getSelectedRows()).length);
/* 130 */     for (int selectedRow : this.myTable.getSelectedRows()) {
/* 131 */       removalSpecs.add(LabelItemSpecWithItems.createForRemove(this.myTableModel.getItem(selectedRow)));
/*     */     }
/* 133 */     this.myLabelModel.addAll(removalSpecs);
/* 134 */     reloadItems();
/*     */   }
/*     */   
/*     */   private void reloadItems() {
/* 138 */     this.myTableModel.setContent(this.myLabelModel.calculateItemsToDisplay());
/* 139 */     ((Listener)this.myEventDispatcher.getMulticaster()).dataChanged(getLabelName(), this.myTableModel.getRowCount());
/*     */   }
/*     */   
/*     */   public void addItems() {
/* 143 */     AddItemDialog d = new AddItemDialog(this.myProject, this.myWorkspace, this.mySourcePath);
/* 144 */     if (d.showAndGet()) {
/*     */       
/* 146 */       this.myLabelModel.add(d.getLabelSpec());
/* 147 */       reloadItems();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/* 152 */   private void updateButtons() { this.myRemoveButton.setEnabled((this.myTable.getSelectedRowCount() > 0)); }
/*     */ 
/*     */ 
/*     */   
/* 156 */   public JPanel getContentPane() { return this.myContentPane; }
/*     */ 
/*     */ 
/*     */   
/* 160 */   public String getLabelName() { return this.myLabelNameTextField.getText().trim(); }
/*     */ 
/*     */ 
/*     */   
/* 164 */   public String getLabelComment() { return this.myLabelCommentTextArea.getText(); }
/*     */ 
/*     */ 
/*     */   
/* 168 */   public List<LabelItemSpec> getLabelItemSpecs() { return this.myLabelModel.getLabelItemSpecs(); }
/*     */ 
/*     */ 
/*     */   
/* 172 */   public void addListener(Listener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 176 */   public void removeListener(Listener listener) { this.myEventDispatcher.addListener(listener); }
/*     */   
/*     */   public static interface Listener extends EventListener {
/*     */     void dataChanged(String param1String, int param1Int);
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ApplyLabelForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */