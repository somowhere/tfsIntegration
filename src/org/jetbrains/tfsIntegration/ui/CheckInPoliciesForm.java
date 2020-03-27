/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.ui.DoubleClickListener;
/*     */ import com.intellij.ui.IdeBorderFactory;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.ui.table.TableView;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.ArrayUtilRt;
/*     */ import com.intellij.util.ui.ColumnInfo;
/*     */ import com.intellij.util.ui.ListTableModel;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import java.awt.Color;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.DefaultComboBoxModel;
/*     */ import javax.swing.DefaultListCellRenderer;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JCheckBox;
/*     */ import javax.swing.JComboBox;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JList;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTable;
/*     */ import javax.swing.event.ListSelectionEvent;
/*     */ import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.table.DefaultTableCellRenderer;
/*     */ import javax.swing.table.TableCellRenderer;
/*     */ import org.jdom.Element;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.checkin.CheckinPoliciesManager;
/*     */ import org.jetbrains.tfsIntegration.checkin.DuplicatePolicyIdException;
/*     */ import org.jetbrains.tfsIntegration.checkin.PolicyBase;
/*     */ import org.jetbrains.tfsIntegration.checkin.StatefulPolicyDescriptor;
/*     */ import org.jetbrains.tfsIntegration.checkin.StatefulPolicyParser;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.TFSVcs;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.TfsCheckinPoliciesCompatibility;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
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
/*     */ public class CheckInPoliciesForm
/*     */ {
/*     */   private final ColumnInfo[] COLUMNS;
/*     */   private JComboBox myProjectCombo;
/*     */   private JButton myAddButton;
/*     */   private JButton myEditButton;
/*     */   private TableView<Pair<StatefulPolicyDescriptor, Boolean>> myPoliciesTable;
/*     */   private JButton myRemoveButton;
/*     */   private JPanel myContentPane;
/*     */   private JCheckBox myTeampriseCheckBox;
/*     */   private JCheckBox myNonInstalledPoliciesCheckBox;
/*     */   private JCheckBox myTeamExplorerCheckBox;
/*     */   private JCheckBox myOverrideCheckBox;
/*     */   private final Project myProject;
/*     */   private final Map<String, ModifyableProjectEntry> myProjectToDescriptors;
/*     */   
/*     */   private static class ModifyableProjectEntry
/*     */     extends ManageWorkspacesForm.ProjectEntry
/*     */   {
/*     */     public boolean isModified;
/*     */     
/*     */     ModifyableProjectEntry(ManageWorkspacesForm.ProjectEntry entry) {
/* 105 */       this.isModified = false;
/*     */ 
/*     */       
/* 108 */       this.descriptors = entry.descriptors;
/* 109 */       this.policiesCompatibilityOverride = entry.policiesCompatibilityOverride;
/*     */     } }
/*     */   public CheckInPoliciesForm(Project project, Map<String, ManageWorkspacesForm.ProjectEntry> projectToDescriptors) {

/*     */     this.COLUMNS = new ColumnInfo[] { new ColumnInfo<Pair<StatefulPolicyDescriptor, Boolean>, Boolean>("Enabled") { public Boolean valueOf(Pair<StatefulPolicyDescriptor, Boolean> item) { return Boolean.valueOf(((StatefulPolicyDescriptor)item.first).isEnabled()); }
/*     */           public Class getColumnClass() { return Boolean.class; }
/*     */           public int getWidth(JTable table) { return 60; }
/*     */           public boolean isCellEditable(Pair<StatefulPolicyDescriptor, Boolean> item) { return true; }
/*     */           public void setValue(Pair<StatefulPolicyDescriptor, Boolean> item, Boolean value) {
/*     */             ((StatefulPolicyDescriptor)item.first).setEnabled(value);
/*     */             ((CheckInPoliciesForm.ModifyableProjectEntry)CheckInPoliciesForm.this.myProjectToDescriptors.get(CheckInPoliciesForm.this.getSelectedProject())).isModified = true;
/*     */           } }
/*     */         , new ColumnInfo<Pair<StatefulPolicyDescriptor, Boolean>, Pair<StatefulPolicyDescriptor, Boolean>>("Policy Type") { public Pair<StatefulPolicyDescriptor, Boolean> valueOf(Pair<StatefulPolicyDescriptor, Boolean> item) { return item; }
/*     */           
/*     */           public TableCellRenderer getRenderer(Pair<StatefulPolicyDescriptor, Boolean> item) { return NAME_RENDERER; } }
/*     */         , new ColumnInfo<Pair<StatefulPolicyDescriptor, Boolean>, Pair<StatefulPolicyDescriptor, Boolean>>("Description") { public Pair<StatefulPolicyDescriptor, Boolean> valueOf(Pair<StatefulPolicyDescriptor, Boolean> item) { return item; }
/*     */           
/*     */           public TableCellRenderer getRenderer(Pair<StatefulPolicyDescriptor, Boolean> item) { return DESCRIPTION_RENDERER; } }
/*     */          };
/* 128 */     this.myProject = project;
/* 129 */     this.myProjectToDescriptors = new HashMap<>(projectToDescriptors.size());
/* 130 */     for (Map.Entry<String, ManageWorkspacesForm.ProjectEntry> e : projectToDescriptors.entrySet()) {
/* 131 */       this.myProjectToDescriptors.put(e.getKey(), new ModifyableProjectEntry(new ModifyableProjectEntry(e.getValue())));
/*     */     }
/*     */     
/* 134 */     this.myProjectCombo.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 137 */             CheckInPoliciesForm.this.updateTable();
/* 138 */             CheckInPoliciesForm.this.updateCheckboxes();
/*     */           }
/*     */         });
/*     */     
/* 142 */     this.myPoliciesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*     */         {
/*     */           public void valueChanged(ListSelectionEvent e) {
/* 145 */             CheckInPoliciesForm.this.updateButtons();
/*     */           }
/*     */         });
/*     */     
/* 149 */     List<String> projects = new ArrayList<>(this.myProjectToDescriptors.keySet());
/* 150 */     Collections.sort(projects, (s1, s2) -> s1.compareTo(s2));
/*     */     
/* 152 */     this.myProjectCombo.setModel(new DefaultComboBoxModel<>(ArrayUtilRt.toStringArray(projects)));
/* 153 */     this.myProjectCombo.setRenderer(new DefaultListCellRenderer()
/*     */         {
/*     */           public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
/* 156 */             JLabel component = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
/* 157 */             String path = (String)value;
/* 158 */             component.setText(VersionControlPath.getTeamProject(path));
/* 159 */             return component;
/*     */           }
/*     */         });
/*     */     
/* 163 */     this.myPoliciesTable.setModelAndUpdateColumns(new ListTableModel(this.COLUMNS));
/*     */     
/* 165 */     this.myEditButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 168 */             StatefulPolicyDescriptor descriptor = CheckInPoliciesForm.this.getSelectedDescriptor();
/* 169 */             CheckInPoliciesForm.this.editPolicy(descriptor);
/*     */           }
/*     */         });
/*     */     
/* 173 */     this.myRemoveButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 176 */             StatefulPolicyDescriptor descriptor = CheckInPoliciesForm.this.getSelectedDescriptor();
/* 177 */             String message = MessageFormat.format("Are you sure to remove checkin policy ''{0}''?", new Object[] { descriptor.getType().getName() });
/* 178 */             if (Messages.showOkCancelDialog(CheckInPoliciesForm.this.myProject, message, "Remove Checkin Policy", Messages.getQuestionIcon()) == 0) {
/* 179 */               CheckInPoliciesForm.ModifyableProjectEntry projectEntry = (CheckInPoliciesForm.ModifyableProjectEntry)CheckInPoliciesForm.this.myProjectToDescriptors.get(CheckInPoliciesForm.this.getSelectedProject());
/* 180 */               projectEntry.descriptors.remove(descriptor);
/* 181 */               projectEntry.isModified = true;
/* 182 */               CheckInPoliciesForm.this.updateTable();
/* 183 */               CheckInPoliciesForm.this.updateButtons();
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 188 */     this.myAddButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 191 */             CheckInPoliciesForm.ModifyableProjectEntry projectEntry = (CheckInPoliciesForm.ModifyableProjectEntry)CheckInPoliciesForm.this.myProjectToDescriptors.get(CheckInPoliciesForm.this.getSelectedProject());
/*     */             
/* 193 */             List<PolicyBase> policies = new ArrayList<>();
/*     */ 
/*     */             
/*     */             try {
/* 197 */               label28: for (PolicyBase installed : CheckinPoliciesManager.getInstalledPolicies()) {
/* 198 */                 if (!installed.canEdit()) {
/* 199 */                   for (StatefulPolicyDescriptor descriptor : projectEntry.descriptors) {
/* 200 */                     if (descriptor.getType().equals(installed.getPolicyType())) {
/*     */                       continue label28;
/*     */                     }
/*     */                   } 
/*     */                 }
/* 205 */                 policies.add(installed);
/*     */               }
/*     */             
/* 208 */             } catch (DuplicatePolicyIdException ex) {
/*     */               
/* 210 */               String message = MessageFormat.format("Several checkin policies with the same id found: ''{0}''.\nPlease review your extensions.", new Object[] { ex.getDuplicateId() });
/* 211 */               Messages.showErrorDialog(CheckInPoliciesForm.this.myProject, message, "Add Checkin Policy");
/*     */               
/*     */               return;
/*     */             } 
/* 215 */             ChooseCheckinPolicyDialog d = new ChooseCheckinPolicyDialog(CheckInPoliciesForm.this.myProject, policies);
/* 216 */             if (!d.showAndGet()) {
/*     */               return;
/*     */             }
/*     */             
/* 220 */             PolicyBase policy = d.getSelectedPolicy();
/*     */ 
/*     */             
/* 223 */             StatefulPolicyDescriptor newDescriptor = new StatefulPolicyDescriptor(policy.getPolicyType(), true, StatefulPolicyParser.createEmptyConfiguration(), Collections.emptyList(), "0", null);
/*     */             
/* 225 */             if (!CheckInPoliciesForm.this.editPolicy(newDescriptor)) {
/*     */               return;
/*     */             }
/*     */             
/* 229 */             projectEntry.descriptors.add(newDescriptor);
/* 230 */             projectEntry.isModified = true;
/* 231 */             CheckInPoliciesForm.this.updateTable();
/* 232 */             int index = projectEntry.descriptors.size() - 1;
/* 233 */             CheckInPoliciesForm.this.myPoliciesTable.getSelectionModel().setSelectionInterval(index, index);
/* 234 */             CheckInPoliciesForm.this.updateButtons();
/*     */           }
/*     */         });
/*     */     
/* 238 */     (new DoubleClickListener()
/*     */       {
/*     */         protected boolean onDoubleClick(MouseEvent e) {
/* 241 */           StatefulPolicyDescriptor descriptor = CheckInPoliciesForm.this.getSelectedDescriptor();
/* 242 */           if (descriptor != null) {
/* 243 */             CheckInPoliciesForm.this.editPolicy(descriptor);
/*     */           }
/* 245 */           return true;
/*     */         }
/* 247 */       }).installOn((Component)this.myPoliciesTable);
/*     */ 
/*     */     
/* 250 */     this.myTeampriseCheckBox.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 253 */             CheckInPoliciesForm.ModifyableProjectEntry entry = (CheckInPoliciesForm.ModifyableProjectEntry)CheckInPoliciesForm.this.myProjectToDescriptors.get(CheckInPoliciesForm.this.getSelectedProject());
/* 254 */             entry.policiesCompatibilityOverride.teamprise = CheckInPoliciesForm.this.myTeampriseCheckBox.isSelected();
/* 255 */             entry.isModified = true;
/*     */           }
/*     */         });
/*     */     
/* 259 */     this.myTeamExplorerCheckBox.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 262 */             CheckInPoliciesForm.ModifyableProjectEntry entry = (CheckInPoliciesForm.ModifyableProjectEntry)CheckInPoliciesForm.this.myProjectToDescriptors.get(CheckInPoliciesForm.this.getSelectedProject());
/* 263 */             entry.policiesCompatibilityOverride.teamExplorer = CheckInPoliciesForm.this.myTeamExplorerCheckBox.isSelected();
/* 264 */             entry.isModified = true;
/*     */           }
/*     */         });
/*     */     
/* 268 */     this.myNonInstalledPoliciesCheckBox.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 271 */             CheckInPoliciesForm.ModifyableProjectEntry entry = (CheckInPoliciesForm.ModifyableProjectEntry)CheckInPoliciesForm.this.myProjectToDescriptors.get(CheckInPoliciesForm.this.getSelectedProject());
/* 272 */             entry.policiesCompatibilityOverride.nonInstalled = CheckInPoliciesForm.this.myNonInstalledPoliciesCheckBox.isSelected();
/* 273 */             entry.isModified = true;
/*     */           }
/*     */         });
/*     */     
/* 277 */     this.myOverrideCheckBox.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 280 */             CheckInPoliciesForm.ModifyableProjectEntry entry = (CheckInPoliciesForm.ModifyableProjectEntry)CheckInPoliciesForm.this.myProjectToDescriptors.get(CheckInPoliciesForm.this.getSelectedProject());
/* 281 */             if (CheckInPoliciesForm.this.myOverrideCheckBox.isSelected()) {
/* 282 */               entry.policiesCompatibilityOverride = new TfsCheckinPoliciesCompatibility(false, false, false);
/*     */             } else {
/*     */               
/* 285 */               entry.policiesCompatibilityOverride = null;
/*     */             } 
/* 287 */             CheckInPoliciesForm.this.updateCheckboxes();
/* 288 */             entry.isModified = true;
/*     */           }
/*     */         });
/*     */     
/* 292 */     ActionListener l = new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/* 295 */           boolean b = (CheckInPoliciesForm.this.myTeamExplorerCheckBox.isSelected() || CheckInPoliciesForm.this.myTeampriseCheckBox.isSelected());
/* 296 */           CheckInPoliciesForm.this.myNonInstalledPoliciesCheckBox.setEnabled(b);
/* 297 */           if (!b) {
/* 298 */             CheckInPoliciesForm.this.myNonInstalledPoliciesCheckBox.setSelected(false);
/* 299 */             ((CheckInPoliciesForm.ModifyableProjectEntry)CheckInPoliciesForm.this.myProjectToDescriptors.get(CheckInPoliciesForm.this.getSelectedProject())).policiesCompatibilityOverride.nonInstalled = false;
/*     */           } 
/*     */         }
/*     */       };
/* 303 */     this.myTeamExplorerCheckBox.addActionListener(l);
/* 304 */     this.myTeampriseCheckBox.addActionListener(l);
/*     */     
/* 306 */     updateTable();
/* 307 */     updateCheckboxes();
/* 308 */     updateButtons();
/*     */   }
/*     */   
/*     */   private void updateCheckboxes() {
/* 312 */     ModifyableProjectEntry entry = this.myProjectToDescriptors.get(getSelectedProject());
/* 313 */     this.myOverrideCheckBox
/* 314 */       .setText(TFSBundle.message("override.policies.compatibility.checkbox", new Object[] { VersionControlPath.getTeamProject(getSelectedProject()) }));
/* 315 */     this.myOverrideCheckBox.setSelected((entry.policiesCompatibilityOverride != null));
/* 316 */     updateSlaveCheckboxes(entry.policiesCompatibilityOverride);
/*     */   }
/*     */   
/*     */   private void updateSlaveCheckboxes(@Nullable TfsCheckinPoliciesCompatibility override) {
/* 320 */     if (override != null) {
/* 321 */       this.myTeampriseCheckBox.setEnabled(true);
/* 322 */       this.myTeampriseCheckBox.setSelected(override.teamprise);
/* 323 */       this.myTeamExplorerCheckBox.setEnabled(true);
/* 324 */       this.myTeamExplorerCheckBox.setSelected(override.teamExplorer);
/* 325 */       this.myNonInstalledPoliciesCheckBox.setSelected(override.nonInstalled);
/* 326 */       this.myNonInstalledPoliciesCheckBox.setEnabled((this.myTeamExplorerCheckBox.isSelected() || this.myTeampriseCheckBox.isSelected()));
/*     */     } else {
/*     */       
/* 329 */       this.myOverrideCheckBox.setSelected(false);
/* 330 */       this.myTeampriseCheckBox.setSelected(false);
/* 331 */       this.myTeampriseCheckBox.setEnabled(false);
/* 332 */       this.myTeamExplorerCheckBox.setSelected(false);
/* 333 */       this.myTeamExplorerCheckBox.setEnabled(false);
/* 334 */       this.myNonInstalledPoliciesCheckBox.setSelected(false);
/* 335 */       this.myNonInstalledPoliciesCheckBox.setEnabled(false);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void updateButtons() {
/* 340 */     StatefulPolicyDescriptor descriptor = getSelectedDescriptor();
/* 341 */     if (descriptor == null) {
/* 342 */       this.myEditButton.setEnabled(false);
/* 343 */       this.myRemoveButton.setEnabled(false);
/*     */     } else {
/*     */       
/*     */       try {
/* 347 */         PolicyBase policy = CheckinPoliciesManager.find(descriptor.getType());
/* 348 */         this.myEditButton.setEnabled((policy != null && canEditSafe(policy)));
/* 349 */         this.myRemoveButton.setEnabled(true);
/*     */       }
/* 351 */       catch (DuplicatePolicyIdException e) {
/*     */         
/* 353 */         throw new RuntimeException((Throwable)e);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void updateTable() {
/* 360 */     List<Pair<StatefulPolicyDescriptor, Boolean>> list = new ArrayList<>(((ModifyableProjectEntry)this.myProjectToDescriptors.get(getSelectedProject())).descriptors.size());
/*     */     try {
/* 362 */       for (StatefulPolicyDescriptor descriptor : ((ModifyableProjectEntry)this.myProjectToDescriptors.get(getSelectedProject())).descriptors) {
/* 363 */         list.add(Pair.create(descriptor, Boolean.valueOf((CheckinPoliciesManager.find(descriptor.getType()) != null))));
/*     */       }
/*     */     }
/* 366 */     catch (DuplicatePolicyIdException e) {
/*     */       
/* 368 */       throw new RuntimeException((Throwable)e);
/*     */     } 
/* 370 */     ((ListTableModel)this.myPoliciesTable.getModel()).setItems(list);
/*     */   }
/*     */ 
/*     */   
/* 374 */   private String getSelectedProject() { return (String)this.myProjectCombo.getSelectedItem(); }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private StatefulPolicyDescriptor getSelectedDescriptor() {
/* 379 */     Pair<StatefulPolicyDescriptor, Boolean> selected = (Pair<StatefulPolicyDescriptor, Boolean>)this.myPoliciesTable.getSelectedObject();
/* 380 */     return (StatefulPolicyDescriptor)Pair.getFirst(selected);
/*     */   }
/*     */ 
/*     */   
/* 384 */   public JComponent getContentPane() { return this.myContentPane; }
/*     */   
/*     */   private boolean editPolicy(StatefulPolicyDescriptor descriptor) {
/*     */     boolean result;
/*     */     PolicyBase policy;
/*     */     try {
/* 390 */       policy = CheckinPoliciesManager.find(descriptor.getType());
/*     */     }
/* 392 */     catch (DuplicatePolicyIdException e) {
/*     */       
/* 394 */       throw new RuntimeException((Throwable)e);
/*     */     } 
/*     */     
/* 397 */     if (policy == null) {
/* 398 */       return false;
/*     */     }
/* 400 */     if (!canEditSafe(policy)) {
/* 401 */       return true;
/*     */     }
/*     */     
/*     */     try {
/* 405 */       policy.loadState(descriptor.getConfiguration().clone());
/*     */     }
/* 407 */     catch (Throwable t) {
/*     */       
/* 409 */       String message = MessageFormat.format("Cannot load state of checkin policy ''{0}'':\n{1}", new Object[] { descriptor.getType().getName(), t.getMessage() });
/* 410 */       Messages.showErrorDialog(this.myProject, message, "Edit Checkin Policy");
/* 411 */       return false;
/*     */     } 
/*     */ 
/*     */     
/*     */     try {
/* 416 */       result = policy.edit(this.myProject);
/*     */     }
/* 418 */     catch (Throwable t) {
/* 419 */       String message = MessageFormat.format("Cannot edit checkin policy ''{0}'':\n{1}", new Object[] { descriptor.getType().getName(), t.getMessage() });
/* 420 */       Messages.showErrorDialog(this.myProject, message, "Edit Checkin Policy");
/* 421 */       return false;
/*     */     } 
/*     */     
/* 424 */     if (result) {
/* 425 */       Element configurationElement = StatefulPolicyParser.createEmptyConfiguration();
/*     */       try {
/* 427 */         policy.saveState(configurationElement);
/* 428 */         descriptor.setConfiguration(configurationElement);
/* 429 */         ((ModifyableProjectEntry)this.myProjectToDescriptors.get(getSelectedProject())).isModified = true;
/*     */       }
/* 431 */       catch (Throwable t) {
/*     */         
/* 433 */         String message = MessageFormat.format("Cannot save state of checkin policy ''{0}'':\n{1}", new Object[] { descriptor.getType().getName(), t.getMessage() });
/* 434 */         Messages.showErrorDialog(this.myProject, message, "Edit Checkin Policy");
/*     */       } 
/*     */     } 
/* 437 */     return result;
/*     */   }
/*     */   
/*     */   private static boolean canEditSafe(PolicyBase policy) {
/*     */     try {
/* 442 */       return policy.canEdit();
/*     */     }
/* 444 */     catch (Throwable t) {
/* 445 */       TFSVcs.LOG.warn(t);
/* 446 */       return false;
/*     */     } 
/*     */   }
/*     */   private static abstract class MyRenderer extends DefaultTableCellRenderer { private MyRenderer() {}
/*     */     
/* 451 */     private static final Color NOT_INSTALLED_POLICY_COLOR = UIUtil.getInactiveTextColor();
/*     */     
/*     */     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
/*     */       Color foreground;
/* 455 */       JLabel component = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/* 456 */       Pair<StatefulPolicyDescriptor, Boolean> item = (Pair<StatefulPolicyDescriptor, Boolean>)value;
/* 457 */       component.setText(getValue((StatefulPolicyDescriptor)item.first));
/*     */       
/* 459 */       if (isSelected) {
/* 460 */         foreground = table.getSelectionForeground();
/*     */       } else {
/*     */         
/* 463 */         foreground = ((Boolean)item.second).booleanValue() ? table.getForeground() : NOT_INSTALLED_POLICY_COLOR;
/*     */       } 
/* 465 */       component.setForeground(foreground);
/* 466 */       return component;
/*     */     }
/*     */     
/*     */     protected abstract String getValue(StatefulPolicyDescriptor param1StatefulPolicyDescriptor); }
/*     */ 
/*     */   
/* 472 */   private static final MyRenderer NAME_RENDERER = new MyRenderer()
/*     */     {
/*     */       protected String getValue(StatefulPolicyDescriptor descriptor) {
/* 475 */         return descriptor.getType().getName();
/*     */       }
/*     */     };
/*     */   
/* 479 */   private static final MyRenderer DESCRIPTION_RENDERER = new MyRenderer()
/*     */     {
/*     */       protected String getValue(StatefulPolicyDescriptor descriptor) {
/* 482 */         return descriptor.getType().getDescription();
/*     */       }
/*     */ 
/*     */       
/*     */       public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
/* 487 */         JLabel component = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/* 488 */         Pair<StatefulPolicyDescriptor, Boolean> item = (Pair<StatefulPolicyDescriptor, Boolean>)value;
/* 489 */         component.setToolTipText(getValue((StatefulPolicyDescriptor)item.first));
/* 490 */         return component;
/*     */       }
/*     */     };
/*     */   
/*     */   public Map<String, ManageWorkspacesForm.ProjectEntry> getModifications() {
/* 495 */     Map<String, ManageWorkspacesForm.ProjectEntry> result = new HashMap<>();
/* 496 */     for (Map.Entry<String, ModifyableProjectEntry> entry : this.myProjectToDescriptors.entrySet()) {
/* 497 */       if (((ModifyableProjectEntry)entry.getValue()).isModified) {
/* 498 */         result.put(entry.getKey(), entry.getValue());
/*     */       }
/*     */     } 
/* 501 */     return result;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\CheckInPoliciesForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */