/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.ui.DocumentAdapter;
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
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.util.Date;
/*     */ import java.util.EventListener;
/*     */ import javax.swing.ButtonGroup;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JRadioButton;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.ChangesetVersionSpec;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.DateVersionSpec;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.LabelVersionSpec;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.version.WorkspaceVersionSpec;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SelectRevisionForm
/*     */ {
/*  45 */   private static final DateFormat DATE_FORMAT = DateFormat.getInstance();
/*     */   
/*     */   private JPanel myPanel;
/*     */   
/*     */   private JRadioButton latestRadioButton;
/*     */   
/*     */   private JRadioButton dateRadioButton;
/*     */   private JRadioButton changesetRadioButton;
/*     */   private JRadioButton labelRadioButton;
/*     */   private JRadioButton workspaceRadioButton;
/*     */   private TextFieldWithBrowseButton.NoPathCompletion labelVersionText;
/*     */   private TextFieldWithBrowseButton.NoPathCompletion changesetVersionText;
/*     */   private JTextField dateText;
/*     */   private JTextField workspaceText;
/*     */   private WorkspaceInfo myWorkspace;
/*     */   private Project myProject;
/*     */   private String myServerPath;
/*     */   private boolean myIsDirectory;
/*     */   private final EventDispatcher<Listener> myEventDispatcher;
/*     */   
/*     */   public SelectRevisionForm(Project project, WorkspaceInfo workspace, String serverPath, boolean isDirectory) {
/*  66 */     this();
/*  67 */     init(project, workspace, serverPath, isDirectory);
/*     */   } public SelectRevisionForm() {
/*     */
/*     */     this.myEventDispatcher = EventDispatcher.create(Listener.class);
/*  71 */     DocumentAdapter documentAdapter = new DocumentAdapter()
/*     */       {
/*     */         protected void textChanged(@NotNull DocumentEvent e1) {
/*  74 */              ((SelectRevisionForm.Listener)SelectRevisionForm.this.myEventDispatcher.getMulticaster()).revisionChanged();
/*     */         }
/*     */       };
/*     */     
/*  78 */     this.labelVersionText.getTextField().getDocument().addDocumentListener((DocumentListener)documentAdapter);
/*  79 */     this.changesetVersionText.getTextField().getDocument().addDocumentListener((DocumentListener)documentAdapter);
/*  80 */     this.dateText.getDocument().addDocumentListener((DocumentListener)documentAdapter);
/*  81 */     this.workspaceText.getDocument().addDocumentListener((DocumentListener)documentAdapter);
/*     */     
/*  83 */     ActionListener radioButtonListener = new ActionListener()
/*     */       {
/*     */         public void actionPerformed(ActionEvent e) {
/*  86 */           SelectRevisionForm.this.updateContols();
/*  87 */           ((SelectRevisionForm.Listener)SelectRevisionForm.this.myEventDispatcher.getMulticaster()).revisionChanged();
/*     */         }
/*     */       };
/*     */     
/*  91 */     this.latestRadioButton.addActionListener(radioButtonListener);
/*  92 */     this.dateRadioButton.addActionListener(radioButtonListener);
/*  93 */     this.changesetRadioButton.addActionListener(radioButtonListener);
/*  94 */     this.labelRadioButton.addActionListener(radioButtonListener);
/*  95 */     this.workspaceRadioButton.addActionListener(radioButtonListener);
/*     */     
/*  97 */     this.changesetVersionText.getButton().addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 100 */             SelectChangesetDialog d = new SelectChangesetDialog(SelectRevisionForm.this.myProject, SelectRevisionForm.this.myWorkspace, SelectRevisionForm.this.myServerPath, SelectRevisionForm.this.myIsDirectory);
/* 101 */             if (d.showAndGet()) {
/* 102 */               SelectRevisionForm.this.changesetVersionText.setText(String.valueOf(d.getChangeset()));
/*     */             }
/*     */           }
/*     */         });
/*     */     
/* 107 */     this.labelVersionText.getButton().addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 110 */             SelectLabelDialog d = new SelectLabelDialog(SelectRevisionForm.this.myProject, SelectRevisionForm.this.myWorkspace);
/* 111 */             if (d.showAndGet()) {
/* 112 */               SelectRevisionForm.this.labelVersionText.setText(d.getLabelString());
/*     */             }
/*     */           }
/*     */         });
/*     */     
/* 117 */     this.latestRadioButton.setSelected(true);
/*     */   }
/*     */   
/*     */   public void init(Project project, WorkspaceInfo workspace, String serverPath, boolean isDirectory) {
/* 121 */     this.myWorkspace = workspace;
/* 122 */     this.myProject = project;
/* 123 */     this.myServerPath = serverPath;
/* 124 */     this.myIsDirectory = isDirectory;
/*     */   }
/*     */   
/*     */   private void updateContols() {
/* 128 */     this.workspaceText.setEnabled(this.workspaceRadioButton.isSelected());
/* 129 */     if (!this.workspaceRadioButton.isSelected()) {
/* 130 */       this.workspaceText.setText(null);
/*     */     } else {
/*     */       
/* 133 */       this.workspaceText.setText(this.myWorkspace.getName() + ';' + TfsUtil.getNameWithoutDomain(this.myWorkspace.getOwnerName()));
/*     */     } 
/*     */     
/* 136 */     this.dateText.setEnabled(this.dateRadioButton.isSelected());
/* 137 */     if (!this.dateRadioButton.isSelected()) {
/* 138 */       this.dateText.setText(null);
/*     */     } else {
/*     */       
/* 141 */       this.dateText.setText(DATE_FORMAT.format(new Date()));
/*     */     } 
/* 143 */     this.labelVersionText.setEnabled(this.labelRadioButton.isSelected());
/* 144 */     if (!this.labelRadioButton.isSelected()) {
/* 145 */       this.labelVersionText.setText(null);
/*     */     }
/* 147 */     this.changesetVersionText.setEnabled(this.changesetRadioButton.isSelected());
/* 148 */     if (!this.changesetRadioButton.isSelected()) {
/* 149 */       this.changesetVersionText.setText(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 154 */   public JPanel getPanel() { return this.myPanel; }
/*     */ 
/*     */   
/*     */   public void setVersionSpec(VersionSpecBase version) {
/* 158 */     this.latestRadioButton.setEnabled(true);
/* 159 */     this.dateRadioButton.setEnabled(true);
/* 160 */     this.changesetRadioButton.setEnabled(true);
/* 161 */     this.labelRadioButton.setEnabled(true);
/* 162 */     this.workspaceRadioButton.setEnabled(true);
/*     */ 
/*     */     
/* 165 */     if (version instanceof LatestVersionSpec) {
/* 166 */       this.latestRadioButton.setSelected(true);
/*     */     }
/* 168 */     else if (version instanceof ChangesetVersionSpec) {
/* 169 */       this.changesetRadioButton.setSelected(true);
/* 170 */       this.changesetVersionText.setEnabled(true);
/* 171 */       this.changesetVersionText.setText(version.getPresentableString());
/*     */     }
/* 173 */     else if (version instanceof WorkspaceVersionSpec) {
/* 174 */       this.workspaceRadioButton.setSelected(true);
/* 175 */       this.workspaceText.setEnabled(true);
/* 176 */       this.workspaceText.setText(version.getPresentableString());
/*     */     }
/* 178 */     else if (version instanceof DateVersionSpec) {
/* 179 */       this.dateRadioButton.setSelected(true);
/* 180 */       this.dateText.setEnabled(true);
/* 181 */       this.dateText.setText(version.getPresentableString());
/*     */     }
/* 183 */     else if (version instanceof LabelVersionSpec) {
/* 184 */       this.labelRadioButton.setSelected(true);
/* 185 */       this.labelVersionText.setEnabled(true);
/* 186 */       this.labelVersionText.setText(version.getPresentableString());
/*     */     } 
/* 188 */     updateContols();
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public VersionSpecBase getVersionSpec() {
/* 193 */     if (this.latestRadioButton.isSelected()) {
/* 194 */       return (VersionSpecBase)LatestVersionSpec.INSTANCE;
/*     */     }
/* 196 */     if (this.changesetRadioButton.isSelected()) {
/*     */       try {
/* 198 */         int changeset = Integer.parseInt(this.changesetVersionText.getText());
/* 199 */         return (VersionSpecBase)new ChangesetVersionSpec(changeset);
/*     */       }
/* 201 */       catch (NumberFormatException e) {
/* 202 */         return null;
/*     */       } 
/*     */     }
/* 205 */     if (this.workspaceRadioButton.isSelected()) {
/* 206 */       return (VersionSpecBase)parseWorkspaceVersionSpec(this.workspaceText.getText());
/*     */     }
/* 208 */     if (this.dateRadioButton.isSelected()) {
/*     */       try {
/* 210 */         return (VersionSpecBase)new DateVersionSpec(DATE_FORMAT.parse(this.dateText.getText()));
/*     */       }
/* 212 */       catch (ParseException e) {
/* 213 */         return null;
/*     */       } 
/*     */     }
/* 216 */     if (this.labelRadioButton.isSelected()) {
/* 217 */       if (this.labelVersionText.getText().trim().length() > 0) {
/* 218 */         return (VersionSpecBase)LabelVersionSpec.fromStringRepresentation(this.labelVersionText.getText());
/*     */       }
/*     */       
/* 221 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 225 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private WorkspaceVersionSpec parseWorkspaceVersionSpec(String versionSpec) {
/* 230 */     if (versionSpec == null || versionSpec.length() == 0 || versionSpec.charAt(0) == ';') {
/* 231 */       return null;
/*     */     }
/*     */     
/* 234 */     int semicolonIndex = versionSpec.indexOf(';');
/* 235 */     String workspaceName = (semicolonIndex < 0) ? versionSpec : versionSpec.substring(0, semicolonIndex);
/* 236 */     if (!WorkspaceInfo.isValidName(workspaceName)) {
/* 237 */       return null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 242 */     String ownerName = (semicolonIndex < 0 || semicolonIndex == versionSpec.length() - 1) ? TfsUtil.getNameWithoutDomain(this.myWorkspace.getOwnerName()) : versionSpec.substring(semicolonIndex + 1);
/*     */     
/* 244 */     int newLength = ownerName.length();
/* 245 */     while (newLength > 0 && ownerName.charAt(newLength - 1) == ' ') {
/* 246 */       newLength--;
/*     */     }
/*     */     
/* 249 */     if (newLength == 0) {
/* 250 */       ownerName = TfsUtil.getNameWithoutDomain(this.myWorkspace.getOwnerName());
/*     */     }
/* 252 */     else if (newLength < ownerName.length()) {
/* 253 */       ownerName = ownerName.substring(0, newLength);
/*     */     } 
/*     */     
/* 256 */     return new WorkspaceVersionSpec(workspaceName, ownerName);
/*     */   }
/*     */   
/*     */   public void disable() {
/* 260 */     this.latestRadioButton.setSelected(true);
/*     */     
/* 262 */     this.latestRadioButton.setEnabled(false);
/* 263 */     this.dateRadioButton.setEnabled(false);
/* 264 */     this.changesetRadioButton.setEnabled(false);
/* 265 */     this.labelRadioButton.setEnabled(false);
/* 266 */     this.workspaceRadioButton.setEnabled(false);
/*     */     
/* 268 */     updateContols();
/*     */   }
/*     */ 
/*     */   
/* 272 */   public void addListener(Listener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */ 
/*     */   
/* 276 */   public void removeListener(Listener listener) { this.myEventDispatcher.removeListener(listener); }
/*     */   
/*     */   public static interface Listener extends EventListener {
/*     */     void revisionChanged();
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\SelectRevisionForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */