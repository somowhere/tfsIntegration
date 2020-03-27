/*     */ package org.jetbrains.tfsIntegration.ui;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.ui.DoubleClickListener;
/*     */ import com.intellij.ui.IdeBorderFactory;
/*     */ import com.intellij.ui.components.JBScrollPane;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Annotation;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.awt.event.ActionListener;
/*     */ import java.awt.event.MouseEvent;
/*     */ import java.io.IOException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.EventListener;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.swing.BorderFactory;
/*     */ import javax.swing.JButton;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
/*     */ import javax.swing.tree.DefaultMutableTreeNode;
/*     */ import org.jdom.JDOMException;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.checkin.CheckinPoliciesManager;
/*     */ import org.jetbrains.tfsIntegration.checkin.DuplicatePolicyIdException;
/*     */ import org.jetbrains.tfsIntegration.checkin.PolicyParseException;
/*     */ import org.jetbrains.tfsIntegration.checkin.StatefulPolicyDescriptor;
/*     */ import org.jetbrains.tfsIntegration.checkin.StatefulPolicyParser;
/*     */ import org.jetbrains.tfsIntegration.config.TfsServerConnectionHelper;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.TfsCheckinPoliciesCompatibility;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.Workstation;
/*     */ import org.jetbrains.tfsIntegration.exceptions.OperationFailedException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.UserCancelledException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.WorkspaceNotFoundException;
/*     */ import org.jetbrains.tfsIntegration.ui.treetable.CellRenderer;
/*     */ import org.jetbrains.tfsIntegration.ui.treetable.ContentProvider;
/*     */ import org.jetbrains.tfsIntegration.ui.treetable.CustomTreeTable;
/*     */ import org.jetbrains.tfsIntegration.ui.treetable.TreeTableColumn;
import org.jetbrains.tfsIntegration.webservice.TfsRequestManager;

/*     */
/*     */ public class ManageWorkspacesForm {
/*  65 */   public static class ProjectEntry { public List<StatefulPolicyDescriptor> descriptors = new ArrayList<>();
/*     */     @Nullable
/*     */     public TfsCheckinPoliciesCompatibility policiesCompatibilityOverride; }
/*     */   
/*  69 */   private static final TreeTableColumn<Object> COLUMN_SERVER_WORKSPACE = new TreeTableColumn<Object>("Server / workspace", 200)
/*     */     {
/*     */       public String getPresentableString(Object value) {
/*  72 */         if (value instanceof ServerInfo) {
/*  73 */           ServerInfo server = (ServerInfo)value;
/*  74 */           if (server.getQualifiedUsername() != null) {
/*  75 */             return MessageFormat.format("{0} [{1}]", new Object[] { server.getPresentableUri(), server.getQualifiedUsername() });
/*     */           }
/*     */           
/*  78 */           return server.getPresentableUri();
/*     */         } 
/*     */         
/*  81 */         if (value instanceof WorkspaceInfo) {
/*  82 */           return ((WorkspaceInfo)value).getName();
/*     */         }
/*  84 */         return "";
/*     */       }
/*     */     };
/*     */   
/*  88 */   private static final TreeTableColumn<Object> COLUMN_SERVER = new TreeTableColumn<Object>("Server", 200)
/*     */     {
/*     */       public String getPresentableString(Object value) {
/*  91 */         if (value instanceof ServerInfo) {
/*  92 */           ServerInfo server = (ServerInfo)value;
/*  93 */           if (server.getQualifiedUsername() != null) {
/*  94 */             return MessageFormat.format("{0} [{1}]", new Object[] { server.getPresentableUri(), server.getQualifiedUsername() });
/*     */           }
/*     */           
/*  97 */           return server.getPresentableUri();
/*     */         } 
/*     */         
/* 100 */         return "";
/*     */       }
/*     */     };
/*     */   
/* 104 */   private static final TreeTableColumn<Object> COLUMN_COMMENT = new TreeTableColumn<Object>("Workspace comment", 100)
/*     */     {
/*     */       public String getPresentableString(Object value) {
/* 107 */         if (value instanceof WorkspaceInfo) {
/* 108 */           return ((WorkspaceInfo)value).getComment();
/*     */         }
/* 110 */         return "";
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private JPanel myContentPane;
/*     */   
/*     */   private JButton myAddServerButton;
/*     */   private JButton myRemoveServerButton;
/*     */   private JButton myProxySettingsButton;
/*     */   private JButton myCreateWorkspaceButton;
/*     */   private JButton myEditWorkspaceButton;
/*     */   private JButton myDeleteWorkspaceButton;
/*     */   private CustomTreeTable<Object> myTable;
/*     */   
/*     */   public ManageWorkspacesForm(Project project, boolean editPoliciesButtonVisible) {
/* 126 */     this.myShowWorkspaces = true;
/* 127 */     this.myEventDispatcher = EventDispatcher.create(Listener.class);
/*     */     
/* 129 */     this.mySelectionListener = new ListSelectionListener()
/*     */       {
/*     */         public void valueChanged(ListSelectionEvent e) {
/* 132 */           ManageWorkspacesForm.this.updateButtons();
/* 133 */           ((ManageWorkspacesForm.Listener)ManageWorkspacesForm.this.myEventDispatcher.getMulticaster()).selectionChanged();
/*     */         }
/*     */       };
/*     */     
/* 137 */     this.myContentProvider = new ContentProvider<Object>()
/*     */       {
/*     */         public Collection<?> getRoots()
/*     */         {
/* 141 */           List<ServerInfo> servers = new ArrayList<>(Workstation.getInstance().getServers());
/* 142 */           Collections.sort(servers, (s1, s2) -> s1.getPresentableUri().compareTo(s2.getPresentableUri()));
/* 143 */           return servers;
/*     */         }
/*     */ 
/*     */         
/*     */         public Collection<?> getChildren(@NotNull Object parent) {
/* 148 */            if (parent instanceof ServerInfo && ManageWorkspacesForm.this.myShowWorkspaces) {
/* 149 */             List<WorkspaceInfo> workspaces = new ArrayList<>(((ServerInfo)parent).getWorkspacesForCurrentOwnerAndComputer());
/*     */             
/* 151 */             Collections.sort(workspaces, (o1, o2) -> o1.getName().compareTo(o2.getName()));
/* 152 */             return workspaces;
/*     */           } 
/* 154 */           return Collections.emptyList();
/*     */         }
/*     */       };
/*     */ 
/*     */     
/* 159 */     this.myProject = project;
/*     */     
/* 161 */     this.myAddServerButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 164 */             ManageWorkspacesForm.this.addServer();
/*     */           }
/*     */         });
/*     */     
/* 168 */     this.myRemoveServerButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 172 */             ManageWorkspacesForm.this.removeServer(ManageWorkspacesForm.this.getSelectedServer());
/*     */           }
/*     */         });
/*     */     
/* 176 */     this.myProxySettingsButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 180 */             ManageWorkspacesForm.this.changeProxySettings(ManageWorkspacesForm.this.getSelectedServer());
/*     */           }
/*     */         });
/*     */     
/* 184 */     this.myCreateWorkspaceButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 187 */             ServerInfo server = ManageWorkspacesForm.this.getSelectedServer();
/* 188 */             if (server == null)
/*     */             {
/* 190 */               server = ManageWorkspacesForm.this.getSelectedWorkspace().getServer();
/*     */             }
/* 192 */             ManageWorkspacesForm.this.createWorkspace(server);
/*     */           }
/*     */         });
/*     */     
/* 196 */     this.myEditWorkspaceButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 200 */             ManageWorkspacesForm.this.editWorkspace(ManageWorkspacesForm.this.getSelectedWorkspace());
/*     */           }
/*     */         });
/*     */     
/* 204 */     this.myDeleteWorkspaceButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e)
/*     */           {
/* 208 */             ManageWorkspacesForm.this.deleteWorkspace(ManageWorkspacesForm.this.getSelectedWorkspace());
/*     */           }
/*     */         });
/*     */     
/* 212 */     this.myCheckInPoliciesButton.setVisible(editPoliciesButtonVisible);
/*     */     
/* 214 */     this.myCheckInPoliciesButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 217 */             ManageWorkspacesForm.this.configureCheckinPolicies();
/*     */           }
/*     */         });
/*     */     
/* 221 */     this.myReloadWorkspacesButton.addActionListener(new ActionListener()
/*     */         {
/*     */           public void actionPerformed(ActionEvent e) {
/* 224 */             ManageWorkspacesForm.this.reloadWorkspaces(ManageWorkspacesForm.this.getSelectedServer());
/*     */           }
/*     */         });
/* 227 */     updateButtons();
/*     */   }
/*     */   private JPanel myWorkspacesPanel; private JButton myCheckInPoliciesButton; private JButton myReloadWorkspacesButton; private final Project myProject; private boolean myShowWorkspaces; private final EventDispatcher<Listener> myEventDispatcher; private final ListSelectionListener mySelectionListener; private final ContentProvider<Object> myContentProvider;
/*     */   private void reloadWorkspaces(ServerInfo server) {
/*     */     try {
/* 232 */       Object selection = getSelectedObject();
/* 233 */       server.refreshWorkspacesForCurrentOwnerAndComputer(this.myContentPane, true);
/* 234 */       updateControls(selection);
/*     */     }
/* 236 */     catch (UserCancelledException userCancelledException) {
/*     */ 
/*     */     
/* 239 */     } catch (TfsException e) {
/* 240 */       Messages.showErrorDialog(this.myContentPane, e.getMessage(), TFSBundle.message("reload.workspaces.title", new Object[0]));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void createUIComponents() {
/* 245 */     this.myTable = new CustomTreeTable(new CellRendererImpl(), false, true);
/* 246 */     this.myTable.getSelectionModel().setSelectionMode(0);
/*     */     
/* 248 */     (new DoubleClickListener()
/*     */       {
/*     */         protected boolean onDoubleClick(MouseEvent event) {
/* 251 */           WorkspaceInfo workspace = ManageWorkspacesForm.this.getSelectedWorkspace();
/* 252 */           if (workspace != null) {
/* 253 */             ManageWorkspacesForm.this.editWorkspace(workspace);
/* 254 */             return true;
/*     */           } 
/* 256 */           return false;
/*     */         }
/* 258 */       }).installOn((Component)this.myTable);
/*     */   }
/*     */   
/*     */   public void setShowWorkspaces(boolean showWorkspaces) {
/* 262 */     this.myShowWorkspaces = showWorkspaces;
/* 263 */     this.myWorkspacesPanel.setVisible(this.myShowWorkspaces);
/* 264 */     this.myReloadWorkspacesButton.setVisible(this.myShowWorkspaces);
/*     */     
/* 266 */     List<TreeTableColumn<Object>> columns = this.myShowWorkspaces ? Arrays.asList((TreeTableColumn<Object>[])new TreeTableColumn[] { COLUMN_SERVER_WORKSPACE, COLUMN_COMMENT }) : Collections.singletonList(COLUMN_SERVER);
/*     */     
/* 268 */     this.myTable.initialize(columns, this.myContentProvider);
/* 269 */     this.myTable.getSelectionModel().addListSelectionListener(this.mySelectionListener);
/* 270 */     this.myTable.expandAll();
/*     */   }
/*     */   
/*     */   private void updateControls(Object selectedServerOrWorkspace) {
/* 274 */     this.myTable.updateContent();
/* 275 */     this.myTable.getSelectionModel().addListSelectionListener(this.mySelectionListener);
/* 276 */     this.myTable.expandAll();
/*     */     
/* 278 */     Object newSelection = null;
/* 279 */     for (int i = 0; i < this.myTable.getModel().getRowCount(); i++) {
/* 280 */       Object o = ((DefaultMutableTreeNode)this.myTable.getModel().getValueAt(i, 0)).getUserObject();
/* 281 */       if (Comparing.equal(o, selectedServerOrWorkspace)) {
/* 282 */         newSelection = o;
/*     */         break;
/*     */       } 
/* 285 */       if (selectedServerOrWorkspace instanceof WorkspaceInfo) {
/* 286 */         WorkspaceInfo selectedWorkspace = (WorkspaceInfo)selectedServerOrWorkspace;
/* 287 */         if (selectedWorkspace.getServer().equals(o)) {
/* 288 */           newSelection = o;
/*     */         }
/* 290 */         if (o instanceof WorkspaceInfo) {
/* 291 */           WorkspaceInfo workspace = (WorkspaceInfo)o;
/* 292 */           if (selectedWorkspace.getName().equals(workspace.getName()) && selectedWorkspace
/* 293 */             .getOwnerName().equals(workspace.getOwnerName()) && selectedWorkspace
/* 294 */             .getServer().equals(workspace.getServer())) {
/* 295 */             newSelection = o;
/*     */             break;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/* 301 */     this.myTable.select(newSelection);
/*     */   }
/*     */   
/*     */   private void updateButtons() {
/* 305 */     ServerInfo selectedServer = getSelectedServer();
/* 306 */     WorkspaceInfo selectedWorkspace = getSelectedWorkspace();
/*     */     
/* 308 */     this.myRemoveServerButton.setEnabled((selectedServer != null));
/* 309 */     this.myProxySettingsButton.setEnabled((selectedServer != null));
/* 310 */     this.myCreateWorkspaceButton.setEnabled((selectedServer != null || selectedWorkspace != null));
/* 311 */     this.myEditWorkspaceButton.setEnabled((selectedWorkspace != null));
/* 312 */     this.myDeleteWorkspaceButton.setEnabled((selectedWorkspace != null));
/* 313 */     this.myCheckInPoliciesButton.setEnabled((selectedServer != null));
/* 314 */     this.myReloadWorkspacesButton.setEnabled((selectedServer != null));
/*     */   }
/*     */   
/*     */   private void addServer() {
/* 318 */     TfsServerConnectionHelper.AddServerResult result = TfsServerConnectionHelper.addServer(getContentPane());
/* 319 */     if (result == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 324 */     if (result.workspacesLoadError != null) {
/* 325 */       Messages.showErrorDialog(this.myContentPane, TFSBundle.message("failed.to.load.workspaces", new Object[] { result.workspacesLoadError
/* 326 */             }), TFSBundle.message("add.server.title", new Object[0]));
/*     */     }
/*     */     
/* 329 */     TFSConfigurationManager.getInstance().storeCredentials(result.uri, result.authorizedCredentials);
/*     */     
/* 331 */     ServerInfo newServer = new ServerInfo(result.uri, result.instanceId, result.workspaces, result.authorizedCredentials.getQualifiedUsername(), result.beans);
/*     */     
/* 333 */     Workstation.getInstance().addServer(newServer);
/* 334 */     List<WorkspaceInfo> workspaces = newServer.getWorkspacesForCurrentOwnerAndComputer();
/* 335 */     updateControls(workspaces.isEmpty() ? newServer : workspaces.iterator().next());
/*     */   }
/*     */   
/*     */   private void removeServer(@NotNull ServerInfo server) {
/* 339 */      String warning = TFSBundle.message("remove.server.prompt", new Object[] { server.getPresentableUri() });
/* 340 */     if (Messages.showYesNoDialog(this.myContentPane, warning, TFSBundle.message("remove.server.title", new Object[0]), Messages.getWarningIcon()) == 0) {
/* 341 */       Workstation.getInstance().removeServer(server);
/* 342 */       updateControls(null);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void changeProxySettings(@NotNull ServerInfo server) {
/* 347 */      ProxySettingsDialog d = new ProxySettingsDialog(this.myProject, server.getUri());
/* 348 */     if (d.showAndGet()) {
/* 349 */       TFSConfigurationManager.getInstance().setProxyUri(server.getUri(), d.getProxyUri());
/*     */     }
/*     */   }
/*     */   
/*     */   private void createWorkspace(@NotNull ServerInfo server) {
/* 354 */      boolean update = false;
/* 355 */     if (TfsRequestManager.shouldShowLoginDialog(server.getUri())) {
/* 356 */       update = true;
/*     */       try {
/* 358 */         TfsServerConnectionHelper.ensureAuthenticated(this.myContentPane, server.getUri(), true);
/*     */       }
/* 360 */       catch (UserCancelledException e) {
/*     */         
/*     */         return;
/* 363 */       } catch (TfsException e) {
/* 364 */         Messages.showErrorDialog(getContentPane(), e.getMessage(), TFSBundle.message("create.workspace", new Object[0]));
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 369 */     WorkspaceDialog d = new WorkspaceDialog(this.myProject, server);
/* 370 */     if (d.showAndGet()) {
/*     */       
/*     */       try {
/* 373 */         WorkspaceInfo newWorkspace = new WorkspaceInfo(server, server.getQualifiedUsername(), Workstation.getComputerName());
/* 374 */         newWorkspace.setName(d.getWorkspaceName());
/* 375 */         newWorkspace.setLocation(d.getWorkspaceLocation());
/* 376 */         newWorkspace.setComment(d.getWorkspaceComment());
/* 377 */         newWorkspace.setWorkingFolders(d.getWorkingFolders());
/* 378 */         newWorkspace.saveToServer(this.myContentPane, null);
/* 379 */         updateControls(newWorkspace);
/*     */         
/*     */         return;
/* 382 */       } catch (UserCancelledException userCancelledException) {
/*     */ 
/*     */       
/* 385 */       } catch (TfsException e) {
/* 386 */         Messages.showErrorDialog(this.myProject, e.getMessage(), TFSBundle.message("create.workspace.title", new Object[0]));
/*     */       } 
/*     */     }
/* 389 */     if (update) {
/* 390 */       updateControls(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void editWorkspace(@NotNull WorkspaceInfo workspace) {
/* 396 */     try { workspace.loadFromServer(this.myContentPane, true); }
/*     */     
/* 398 */     catch (WorkspaceNotFoundException e)
/* 399 */     { Messages.showErrorDialog(this.myProject, e.getMessage(), TFSBundle.message("edit.workspace.title", new Object[0]));
/*     */       try {
/* 401 */         workspace.getServer().refreshWorkspacesForCurrentOwnerAndComputer(this.myContentPane, true);
/* 402 */         updateControls(null);
/*     */       }
/* 404 */       catch (UserCancelledException userCancelledException) {
/*     */ 
/*     */       
/* 407 */       } catch (TfsException e2) {
/* 408 */         Messages.showErrorDialog(this.myContentPane, e2.getMessage(), TFSBundle.message("reload.workspaces.title", new Object[0]));
/*     */       } 
/*     */       
/*     */       return; }
/* 412 */     catch (UserCancelledException e)
/*     */     
/*     */     { return; }
/* 415 */     catch (TfsException e)
/* 416 */     { Messages.showErrorDialog(this.myProject, e.getMessage(), TFSBundle.message("edit.workspace.title", new Object[0]));
/*     */       
/*     */       return; }
/*     */     
/* 420 */     WorkspaceInfo modifiedWorkspace = workspace.getCopy();
/* 421 */     WorkspaceDialog d = new WorkspaceDialog(this.myProject, modifiedWorkspace);
/* 422 */     if (d.showAndGet()) {
/* 423 */       modifiedWorkspace.setName(d.getWorkspaceName());
/* 424 */       modifiedWorkspace.setLocation(d.getWorkspaceLocation());
/* 425 */       modifiedWorkspace.setComment(d.getWorkspaceComment());
/* 426 */       modifiedWorkspace.setWorkingFolders(d.getWorkingFolders());
/*     */       try {
/* 428 */         modifiedWorkspace.saveToServer(this.myContentPane, workspace);
/* 429 */         updateControls(modifiedWorkspace);
/*     */       }
/* 431 */       catch (UserCancelledException userCancelledException) {
/*     */ 
/*     */       
/* 434 */       } catch (TfsException e) {
/* 435 */         Messages.showErrorDialog(this.myProject, e.getMessage(), TFSBundle.message("save.workspace.title", new Object[0]));
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void deleteWorkspace(@NotNull WorkspaceInfo workspace) {
/* 441 */      if (Messages.showYesNoDialog(this.myContentPane, TFSBundle.message("delete.workspace.prompt", new Object[] { workspace.getName()
/* 442 */           }), TFSBundle.message("delete.workspace.title", new Object[0]), Messages.getWarningIcon()) != 0) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/* 447 */       workspace.getServer().deleteWorkspace(workspace, this.myContentPane, true);
/* 448 */       updateControls(workspace);
/*     */     }
/* 450 */     catch (UserCancelledException userCancelledException) {
/*     */ 
/*     */     
/* 453 */     } catch (TfsException e) {
/* 454 */       Messages.showErrorDialog(this.myProject, e.getMessage(), TFSBundle.message("delete.workspace.title", new Object[0]));
/*     */     } 
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   private Object getSelectedObject() {
/* 460 */     if (this.myTable.getSelectedRowCount() == 1) {
/* 461 */       Collection<Object> selection = this.myTable.getSelectedItems();
/* 462 */       if (selection.size() == 1) {
/* 463 */         return this.myTable.getSelectedItems().iterator().next();
/*     */       }
/*     */     } 
/* 466 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public WorkspaceInfo getSelectedWorkspace() {
/* 471 */     Object selectedObject = getSelectedObject();
/* 472 */     if (selectedObject instanceof WorkspaceInfo) {
/* 473 */       return (WorkspaceInfo)selectedObject;
/*     */     }
/* 475 */     return null;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public ServerInfo getSelectedServer() {
/* 480 */     Object selectedObject = getSelectedObject();
/* 481 */     if (selectedObject instanceof ServerInfo) {
/* 482 */       return (ServerInfo)selectedObject;
/*     */     }
/* 484 */     if (selectedObject instanceof WorkspaceInfo) {
/* 485 */       return ((WorkspaceInfo)selectedObject).getServer();
/*     */     }
/* 487 */     return null;
/*     */   }
/*     */ 
/*     */   
/* 491 */   public void setSelectedWorkspace(WorkspaceInfo workspace) { this.myTable.select(workspace); }
/*     */ 
/*     */ 
/*     */   
/* 495 */   public void setSelectedServer(ServerInfo server) { this.myTable.select(server); }
/*     */ 
/*     */   
/*     */   public void selectFirstServer() {
/* 499 */     Collection<?> servers = this.myContentProvider.getRoots();
/* 500 */     if (!servers.isEmpty()) {
/* 501 */       this.myTable.select(servers.iterator().next());
/*     */     }
/*     */   }
/*     */   
/*     */   public void selectFirstWorkspace() {
/* 506 */     Collection<?> servers = this.myContentProvider.getRoots();
/* 507 */     for (Object server : servers) {
/* 508 */       Collection<?> workspaces = this.myContentProvider.getChildren(server);
/* 509 */       if (!workspaces.isEmpty()) {
/* 510 */         this.myTable.select(workspaces.iterator().next());
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/* 517 */   public JComponent getContentPane() { return this.myContentPane; }
/*     */ 
/*     */ 
/*     */   
/* 521 */   public void addSelectionListener(Listener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */   
/*     */   private static class CellRendererImpl
/*     */     extends CellRenderer<Object>
/*     */   {
/*     */     private CellRendererImpl() {}
/*     */     
/*     */     protected void render(CustomTreeTable<Object> treeTable, TreeTableColumn<Object> column, Object value, JLabel cell) {
/* 530 */       super.render(treeTable, column, value, cell);
/*     */       
/* 532 */       if (column == COLUMN_SERVER_WORKSPACE || column == COLUMN_SERVER) {
/* 533 */         if (value instanceof ServerInfo) {
/* 534 */           cell.setIcon(UiConstants.ICON_SERVER);
/*     */         }
/* 536 */         else if (value instanceof WorkspaceInfo) {
/* 537 */           cell.setIcon(UiConstants.ICON_FILE);
/*     */         } 
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void configureCheckinPolicies() {
/*     */     try {
/* 545 */       CheckinPoliciesManager.getInstalledPolicies();
/*     */     }
/* 547 */     catch (DuplicatePolicyIdException e) {
/*     */       
/* 549 */       String message = MessageFormat.format("Several checkin policies with the same id found: ''{0}''.\nPlease review your extensions.", new Object[] { e.getDuplicateId() });
/* 550 */       Messages.showErrorDialog(this.myProject, message, "Edit Checkin Policies");
/*     */       
/*     */       return;
/*     */     } 
/* 554 */     final ServerInfo server = getSelectedServer();
/*     */     
/* 556 */     TfsExecutionUtil.Process<Map<String, ProjectEntry>> process = new TfsExecutionUtil.Process<Map<String, ProjectEntry>>()
/*     */       {
/*     */         public Map<String, ManageWorkspacesForm.ProjectEntry> run() throws TfsException, VcsException {
/* 559 */           Map<String, ManageWorkspacesForm.ProjectEntry> entries = new HashMap<>();
/*     */           
/* 561 */           List<Item> projectItems = server.getVCS().getChildItems("$/", true, ManageWorkspacesForm.this.myContentPane, null);
/* 562 */           if (projectItems.isEmpty()) {
/* 563 */             throw new OperationFailedException("No team project found");
/*     */           }
/*     */           
/* 566 */           for (Item projectItem : projectItems) {
/* 567 */             ManageWorkspacesForm.ProjectEntry entry = new ManageWorkspacesForm.ProjectEntry();
/*     */ 
/*     */ 
/*     */             
/* 571 */             Collection<Annotation> policiesAnnotations = server.getVCS().queryAnnotations("TeampriseCheckinPolicies", projectItem.getItem(), ManageWorkspacesForm.this.myContentPane, null, true);
/* 572 */             if (!policiesAnnotations.isEmpty()) {
/*     */               try {
/* 574 */                 entry.descriptors = StatefulPolicyParser.parseDescriptors(((Annotation)policiesAnnotations.iterator().next()).getValue());
/*     */               }
/* 576 */               catch (PolicyParseException ex) {
/* 577 */                 String message = MessageFormat.format("Cannot load checkin policies definitions:\n{0}", new Object[] { ex.getMessage() });
/* 578 */                 throw new OperationFailedException(message);
/*     */               } 
/*     */             }
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 585 */             Collection<Annotation> overridesAnnotations = server.getVCS().queryAnnotations("IntellijOverrides", projectItem.getItem(), ManageWorkspacesForm.this.myContentPane, null, true);
/* 586 */             if (!overridesAnnotations.isEmpty()) {
/*     */               try {
/* 588 */                 entry
/* 589 */                   .policiesCompatibilityOverride = TfsCheckinPoliciesCompatibility.fromOverridesAnnotationValue(((Annotation)overridesAnnotations.iterator().next()).getValue());
/*     */               }
/* 591 */               catch (IOException ex) {
/* 592 */                 String message = MessageFormat.format("Cannot load checkin policies overrides:\n{0}", new Object[] { ex.getMessage() });
/* 593 */                 throw new OperationFailedException(message);
/*     */               }
/* 595 */               catch (JDOMException ex) {
/* 596 */                 String message = MessageFormat.format("Cannot load checkin policies overrides:\n{0}", new Object[] { ex.getMessage() });
/* 597 */                 throw new OperationFailedException(message);
/*     */               } 
/*     */             }
/* 600 */             entries.put(projectItem.getItem(), entry);
/*     */           } 
/* 602 */           return entries;
/*     */         }
/*     */       };
/*     */ 
/*     */     
/* 607 */     TfsExecutionUtil.ResultWithError<Map<String, ProjectEntry>> loadResult = TfsExecutionUtil.executeInBackground("Loading Checkin Policies", this.myProject, process);
/* 608 */     if (loadResult.cancelled || loadResult.showDialogIfError("Configure Checkin Policies")) {
/*     */       return;
/*     */     }
/*     */     
/* 612 */     Map<String, ProjectEntry> projectToDescriptors = (Map<String, ProjectEntry>)loadResult.result;
/* 613 */     CheckInPoliciesDialog d = new CheckInPoliciesDialog(this.myProject, getSelectedServer(), projectToDescriptors);
/* 614 */     if (d.showAndGet()) {
/* 615 */       final Map<String, ProjectEntry> modifications = d.getModifications();
/* 616 */       if (!modifications.isEmpty()) {
/*     */         
/* 618 */         TfsExecutionUtil.ResultWithError<Void> saveResult = TfsExecutionUtil.executeInBackground("Saving Checkin Policies", this.myProject, new TfsExecutionUtil.VoidProcess()
/*     */             {
/*     */               public void run() throws TfsException, VcsException {
/* 621 */                 for (Map.Entry<String, ManageWorkspacesForm.ProjectEntry> i : (Iterable<Map.Entry<String, ManageWorkspacesForm.ProjectEntry>>)modifications.entrySet()) {
/*     */                   
/* 623 */                   server.getVCS().deleteAnnotation(i.getKey(), "TeampriseCheckinPolicies", ManageWorkspacesForm.this.myContentPane, null);
/* 624 */                   server.getVCS().deleteAnnotation(i.getKey(), "IntellijOverrides", ManageWorkspacesForm.this.myContentPane, null);
/*     */ 
/*     */                   
/* 627 */                   ManageWorkspacesForm.ProjectEntry entry = i.getValue();
/* 628 */                   if (!entry.descriptors.isEmpty()) {
/* 629 */                     String annotationValue = StatefulPolicyParser.saveDescriptors(entry.descriptors);
/* 630 */                     server.getVCS()
/* 631 */                       .createAnnotation(i.getKey(), "TeampriseCheckinPolicies", annotationValue, ManageWorkspacesForm.this.myContentPane, null);
/*     */                   } 
/*     */ 
/*     */                   
/* 635 */                   if (entry.policiesCompatibilityOverride != null) {
/* 636 */                     server.getVCS().createAnnotation(i.getKey(), "IntellijOverrides", entry.policiesCompatibilityOverride
/* 637 */                         .toOverridesAnnotationValue(), ManageWorkspacesForm.this.myContentPane, null);
/*     */                   }
/*     */                 } 
/*     */               }
/*     */             });
/* 642 */         saveResult.showDialogIfError("Save Checkin Policies");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/* 648 */   public JComponent getPreferredFocusedComponent() { return (JComponent)this.myTable; }
/*     */   
/*     */   public static interface Listener extends EventListener {
/*     */     void selectionChanged();
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\ManageWorkspacesForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */