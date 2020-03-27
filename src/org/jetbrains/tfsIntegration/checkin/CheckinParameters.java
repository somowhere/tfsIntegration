/*     */ package org.jetbrains.tfsIntegration.checkin;
/*     */ 
/*     */ import com.intellij.mock.MockProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProcessCanceledException;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Pair;
/*     */ import com.intellij.openapi.util.text.StringUtil;
/*     */ import com.intellij.openapi.vcs.CheckinProjectPanel;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vcs.actions.VcsContextFactory;
/*     */ import com.intellij.util.ArrayUtilRt;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.util.containers.MultiMap;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Annotation;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinNoteFieldDefinition;
/*     */ import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.CheckinWorkItemAction;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.text.MessageFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.jdom.JDOMException;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSVcs;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.TfsCheckinPoliciesCompatibility;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ItemPath;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsExecutionUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkItemsCheckinParameters;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.WorkstationHelper;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.workitems.WorkItem;
/*     */ import org.jetbrains.tfsIntegration.exceptions.OperationFailedException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ 
/*     */ public class CheckinParameters {
/*     */   private final CheckinProjectPanel myPanel;
/*     */   private Map<ServerInfo, ServerData> myData;
/*     */   private boolean myPoliciesEvaluated;
/*     */   private String myOverrideReason;
/*     */   private String myPoliciesLoadError;
/*     */   
/*     */   public static class CheckinNote { @NotNull
/*     */     public final String name;
/*     */     public final boolean required;
/*     */     @Nullable
/*     */     public String value;
/*     */     
/*     */     private CheckinNote(String name, boolean required) {
/*  62 */       this.name = name;
/*  63 */       this.required = required;
/*     */     } }
/*     */ 
/*     */   
/*     */   private static class TeamProjectData {
/*  68 */     public TfsCheckinPoliciesCompatibility myCompatibility = TFSConfigurationManager.getInstance().getCheckinPoliciesCompatibility(); private TeamProjectData() {}
/*  69 */     public final List<PolicyDescriptor> myPolicies = new ArrayList<>();
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ServerData
/*     */   {
/*     */     public final List<CheckinParameters.CheckinNote> myCheckinNotes;
/*     */     
/*     */     public final WorkItemsCheckinParameters myWorkItems;
/*     */     public List<PolicyFailure> myPolicyFailures;
/*     */     public List<String> myEmptyNotes;
/*     */     public final Collection<FilePath> myFiles;
/*     */     public final Map<String, CheckinParameters.TeamProjectData> myPolicies;
/*     */     
/*     */     private ServerData(List<CheckinParameters.CheckinNote> checkinNotes, WorkItemsCheckinParameters workItems, Collection<FilePath> files, Map<String, CheckinParameters.TeamProjectData> policies) {
/*  84 */       this.myCheckinNotes = checkinNotes;
/*  85 */       this.myWorkItems = workItems;
/*  86 */       this.myPolicies = policies;
/*  87 */       this.myFiles = files;
/*     */     }
/*     */   }
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
/*     */   private CheckinParameters(CheckinProjectPanel panel, @NotNull Map<ServerInfo, ServerData> data, boolean policiesEvaluated, String policiesLoadError) {
/* 101 */     this.myPanel = panel;
/* 102 */     this.myData = data;
/* 103 */     this.myPoliciesEvaluated = policiesEvaluated;
/* 104 */     this.myPoliciesLoadError = policiesLoadError;
/*     */   }
/*     */ 
/*     */   
/*     */   public CheckinParameters(final CheckinProjectPanel panel, final boolean evaluatePolicies) throws OperationFailedException {
/* 109 */     this.myPanel = panel;
/* 110 */     final Collection<FilePath> filePaths = new ArrayList<>(panel.getFiles().size());
/* 111 */     for (File file : panel.getFiles()) {
/* 112 */       FilePath filePath = VcsContextFactory.SERVICE.getInstance().createFilePathOn(file);
/* 113 */       if (!TFSVcs.isUnderTFS(filePath, this.myPanel.getProject())) {
/*     */         continue;
/*     */       }
/* 116 */       filePaths.add(filePath);
/*     */     } 
/*     */ 
/*     */     
/* 120 */     TfsExecutionUtil.ResultWithError<Void> result = TfsExecutionUtil.executeInBackground("Validating Checkin", panel.getProject(), new TfsExecutionUtil.VoidProcess()
/*     */         {
/*     */           public void run() throws TfsException, VcsException {
/* 123 */             ProgressIndicator object = ProgressManager.getInstance().getProgressIndicator();
/* 124 */             if (object == null) {
/* 125 */               object = new MockProgressIndicator();
/*     */             }
/* 127 */             object.setText("Loading checkin notes and policy definitions");
/* 128 */             final MultiMap<ServerInfo, String> serverToProjects = MultiMap.createSet();
/* 129 */             final Map<ServerInfo, Collection<FilePath>> serverToFiles = new HashMap<>();
/* 130 */             WorkstationHelper.processByWorkspaces(filePaths, false, panel.getProject(), new WorkstationHelper.VoidProcessDelegate()
/*     */                 {
/*     */                   public void executeRequest(WorkspaceInfo workspace, List<ItemPath> paths) throws TfsException {
/* 133 */                     Collection<FilePath> files = (Collection<FilePath>)serverToFiles.get(workspace.getServer());
/* 134 */                     if (files == null) {
/* 135 */                       files = new ArrayList<>();
/* 136 */                       serverToFiles.put(workspace.getServer(), files);
/*     */                     } 
/* 138 */                     for (ItemPath path : paths) {
/* 139 */                       serverToProjects.putValue(workspace.getServer(), VersionControlPath.getPathToProject(path.getServerPath()));
/* 140 */                       files.add(path.getLocalPath());
/*     */                     } 
/*     */                   }
/*     */                 });
/*     */             
/* 145 */             if (serverToProjects.isEmpty()) {
/* 146 */               throw new OperationFailedException("Team Foundation Server mappings not found.");
/*     */             }
/* 148 */             object.checkCanceled();
/*     */             
/* 150 */             List<ServerInfo> sortedServers = new ArrayList<>(serverToProjects.keySet());
/* 151 */             Collections.sort(sortedServers, (o1, o2) -> o1.getPresentableUri().compareTo(o2.getPresentableUri()));
/*     */             
/* 153 */             Map<ServerInfo, CheckinParameters.ServerData> data = new LinkedHashMap<>();
/* 154 */             StringBuilder policiesLoadError = new StringBuilder();
/* 155 */             for (ServerInfo server : sortedServers) {
/* 156 */               Collection<String> teamProjects = serverToProjects.get(server);
/*     */               
/* 158 */               List<CheckinNoteFieldDefinition> checkinNoteDefinitions = server.getVCS().queryCheckinNoteDefinition(teamProjects, CheckinParameters.this.myPanel.getProject(), null);
/* 159 */               object.checkCanceled();
/* 160 */               Map<String, CheckinNoteFieldDefinition> nameToDefinition = new HashMap<>();
/*     */               
/* 162 */               for (CheckinNoteFieldDefinition definition : checkinNoteDefinitions) {
/* 163 */                 if (!nameToDefinition.containsKey(definition.getName()) || definition.getReq()) {
/* 164 */                   nameToDefinition.put(definition.getName(), definition);
/*     */                 }
/*     */               } 
/* 167 */               List<CheckinNoteFieldDefinition> sortedDefinitions = new ArrayList<>(nameToDefinition.values());
/* 168 */               Collections.sort(sortedDefinitions, (o1, o2) -> o1.get_do() - o2.get_do());
/*     */               
/* 170 */               List<CheckinParameters.CheckinNote> checkinNotes = new ArrayList<>(sortedDefinitions.size());
/* 171 */               for (CheckinNoteFieldDefinition checkinNote : sortedDefinitions) {
/* 172 */                 checkinNotes.add(new CheckinParameters.CheckinNote(checkinNote.getName(), checkinNote.getReq()));
/*     */               }
/*     */               
/* 175 */               Map<String, CheckinParameters.TeamProjectData> project2policies = new HashMap<>();
/* 176 */               for (String teamProject : teamProjects) {
/* 177 */                 project2policies.put(teamProject, new CheckinParameters.TeamProjectData());
/*     */               }
/*     */               
/*     */               try {
/* 181 */                 Collection<Annotation> overridesAnnotations = new ArrayList<>();
/* 182 */                 for (String teamProjectPath : teamProjects) {
/* 183 */                   overridesAnnotations.addAll(server
/* 184 */                       .getVCS().queryAnnotations("IntellijOverrides", teamProjectPath, CheckinParameters.this.myPanel.getProject(), null, false));
/*     */                 }
/*     */                 
/* 187 */                 boolean teamExplorerFound = (TFSConfigurationManager.getInstance().getCheckinPoliciesCompatibility()).teamExplorer;
/* 188 */                 boolean teampriseFound = (TFSConfigurationManager.getInstance().getCheckinPoliciesCompatibility()).teamprise;
/* 189 */                 for (Annotation annotation : overridesAnnotations) {
/* 190 */                   if (annotation.getValue() == null)
/* 191 */                     continue;  String teamProject = VersionControlPath.getPathToProject(annotation.getItem());
/*     */ 
/*     */                   
/* 194 */                   TfsCheckinPoliciesCompatibility override = TfsCheckinPoliciesCompatibility.fromOverridesAnnotationValue(annotation.getValue());
/* 195 */                   ((CheckinParameters.TeamProjectData)project2policies.get(teamProject)).myCompatibility = override;
/* 196 */                   teamExplorerFound |= override.teamExplorer;
/* 197 */                   teampriseFound |= override.teamprise;
/*     */                 } 
/*     */                 
/* 200 */                 if (teamExplorerFound) {
/* 201 */                   Collection<Annotation> annotations = new ArrayList<>();
/* 202 */                   for (String teamProjectPath : teamProjects) {
/* 203 */                     annotations.addAll(server.getVCS()
/* 204 */                         .queryAnnotations("CheckinPolicies", teamProjectPath, CheckinParameters.this
/* 205 */                           .myPanel.getProject(), null, false));
/*     */                   }
/*     */                   
/* 208 */                   for (Annotation annotation : annotations) {
/* 209 */                     if (annotation.getValue() == null)
/* 210 */                       continue;  String teamProject = VersionControlPath.getPathToProject(annotation.getItem());
/*     */                     
/* 212 */                     CheckinParameters.TeamProjectData teamProjectData = project2policies.get(teamProject);
/* 213 */                     if (teamProjectData.myCompatibility.teamExplorer) {
/* 214 */                       for (PolicyDescriptor descriptor : StatelessPolicyParser.parseDescriptors(annotation.getValue())) {
/* 215 */                         if (descriptor.isEnabled()) {
/* 216 */                           teamProjectData.myPolicies.add(descriptor);
/*     */                         }
/*     */                       } 
/*     */                     }
/*     */                   } 
/*     */                 } 
/*     */                 
/* 223 */                 if (teampriseFound) {
/* 224 */                   Collection<Annotation> annotations = new ArrayList<>();
/* 225 */                   for (String teamProjectPath : teamProjects) {
/* 226 */                     annotations.addAll(server.getVCS()
/* 227 */                         .queryAnnotations("TeampriseCheckinPolicies", teamProjectPath, CheckinParameters.this
/* 228 */                           .myPanel.getProject(), null, false));
/*     */                   }
/* 230 */                   for (Annotation annotation : annotations) {
/* 231 */                     if (annotation.getValue() == null)
/* 232 */                       continue;  String teamProject = VersionControlPath.getPathToProject(annotation.getItem());
/*     */                     
/* 234 */                     CheckinParameters.TeamProjectData teamProjectData = project2policies.get(teamProject);
/* 235 */                     if (teamProjectData.myCompatibility.teamprise) {
/* 236 */                       for (PolicyDescriptor descriptor : StatefulPolicyParser.parseDescriptors(annotation.getValue())) {
/* 237 */                         if (descriptor.isEnabled()) {
/* 238 */                           teamProjectData.myPolicies.add(descriptor);
/*     */                         }
/*     */                       }
/*     */                     
/*     */                     }
/*     */                   } 
/*     */                 } 
/* 245 */               } catch (PolicyParseException e) {
/* 246 */                 policiesLoadError.append(e.getMessage());
/*     */               }
/* 248 */               catch (JDOMException e) {
/* 249 */                 policiesLoadError.append(e.getMessage());
/*     */               }
/* 251 */               catch (IOException e) {
/* 252 */                 policiesLoadError.append(e.getMessage());
/*     */               } 
/* 254 */               object.checkCanceled();
/*     */               
/* 256 */               data.put(server, new CheckinParameters.ServerData(checkinNotes, new WorkItemsCheckinParameters(), serverToFiles.get(server), project2policies));
/*     */             } 
/*     */             
/* 259 */             CheckinParameters.this.myPoliciesLoadError = (policiesLoadError.length() > 0) ? policiesLoadError.toString() : null;
/* 260 */             CheckinParameters.this.myData = data;
/*     */             
/* 262 */             if (evaluatePolicies) {
/* 263 */               object.setText("Evaluating checkin policies");
/* 264 */               CheckinParameters.this.evaluatePolicies((ProgressIndicator)object);
/* 265 */               CheckinParameters.this.myPoliciesEvaluated = true;
/*     */             } 
/*     */           }
/*     */         });
/*     */     
/* 270 */     if (this.myData == null) {
/* 271 */       throw new OperationFailedException(result.cancelled ? "Validation cancelled by user" : result.error.getMessage());
/*     */     }
/*     */     
/* 274 */     validateNotes();
/*     */   }
/*     */ 
/*     */   
/* 278 */   public boolean policiesEvaluated() { return this.myPoliciesEvaluated; }
/*     */ 
/*     */ 
/*     */   
/* 282 */   public String getPoliciesLoadError() { return this.myPoliciesLoadError; }
/*     */ 
/*     */   
/*     */   public void evaluatePolicies(ProgressIndicator pi) {
/* 286 */     for (Map.Entry<ServerInfo, ServerData> entry : this.myData.entrySet()) {
/* 287 */       PolicyContext context = createPolicyContext(entry.getKey());
/*     */       
/* 289 */       List<PolicyFailure> allFailures = new ArrayList<>();
/* 290 */       for (Map.Entry<String, TeamProjectData> teamProjectDataEntry : ((ServerData)entry.getValue()).myPolicies.entrySet()) {
/* 291 */         for (PolicyDescriptor descriptor : ((TeamProjectData)teamProjectDataEntry.getValue()).myPolicies) {
/*     */           PolicyBase policy;
/*     */           try {
/* 294 */             policy = CheckinPoliciesManager.find(descriptor.getType());
/*     */           }
/* 296 */           catch (DuplicatePolicyIdException e) {
/*     */             
/* 298 */             String tooltip = MessageFormat.format("Several checkin policies with the same id found: ''{0}''.\nPlease review your extensions.", new Object[] { e.getDuplicateId() });
/* 299 */             allFailures.add(new PolicyFailure(CheckinPoliciesManager.DUMMY_POLICY, "Duplicate checkin policy id", tooltip));
/*     */             
/*     */             break;
/*     */           } 
/* 303 */           if (policy == null) {
/* 304 */             if (((TeamProjectData)teamProjectDataEntry.getValue()).myCompatibility.nonInstalled) {
/* 305 */               allFailures.add(new NotInstalledPolicyFailure(descriptor.getType(), !(descriptor instanceof StatefulPolicyDescriptor)));
/*     */             }
/*     */             
/*     */             continue;
/*     */           } 
/* 310 */           pi.setText(MessageFormat.format("Evaluating checkin policy: {0}", new Object[] { policy.getPolicyType().getName() }));
/* 311 */           pi.setText2("");
/* 312 */           if (descriptor instanceof StatefulPolicyDescriptor) {
/*     */             try {
/* 314 */               policy.loadState(((StatefulPolicyDescriptor)descriptor).getConfiguration().clone());
/*     */             }
/* 316 */             catch (ProcessCanceledException e) {
/* 317 */               throw e;
/*     */             }
/* 319 */             catch (RuntimeException e) {
/* 320 */               TFSVcs.LOG.warn(e);
/*     */               
/* 322 */               String message = MessageFormat.format("Cannot load configuration of checkin policy ''{0}''", new Object[] { policy.getPolicyType().getName() });
/* 323 */               String tooltip = MessageFormat.format("The following error occured while loading: {0}", new Object[] { e.getMessage() });
/* 324 */               allFailures.add(new PolicyFailure(CheckinPoliciesManager.DUMMY_POLICY, message, tooltip));
/*     */               
/*     */               continue;
/*     */             } 
/*     */           }
/*     */           try {
/* 330 */             PolicyFailure[] failures = policy.evaluate(context, pi);
/* 331 */             ContainerUtil.addAll(allFailures, failures);
/*     */           }
/* 333 */           catch (ProcessCanceledException e) {
/* 334 */             throw e;
/*     */           }
/* 336 */           catch (RuntimeException e) {
/* 337 */             TFSVcs.LOG.warn(e);
/* 338 */             String message = MessageFormat.format("Cannot evaluate checkin policy ''{0}''", new Object[] { policy.getPolicyType().getName() });
/* 339 */             String tooltip = MessageFormat.format("The following error occured while evaluating: {0}", new Object[] { e.getMessage() });
/* 340 */             allFailures.add(new PolicyFailure(CheckinPoliciesManager.DUMMY_POLICY, message, tooltip));
/*     */           } 
/* 342 */           pi.checkCanceled();
/*     */         } 
/*     */       } 
/* 345 */       ((ServerData)entry.getValue()).myPolicyFailures = allFailures;
/*     */     } 
/* 347 */     this.myPoliciesEvaluated = true;
/*     */   }
/*     */   
/*     */   public PolicyContext createPolicyContext(ServerInfo server) {
/* 351 */     final ServerData serverData = this.myData.get(server);
/* 352 */     return new PolicyContext()
/*     */       {
/*     */         public Collection<FilePath> getFiles() {
/* 355 */           return Collections.unmodifiableCollection(serverData.myFiles);
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 360 */         public Project getProject() { return CheckinParameters.this.myPanel.getProject(); }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 365 */         public String getCommitMessage() { return CheckinParameters.this.myPanel.getCommitMessage(); }
/*     */ 
/*     */ 
/*     */         
/*     */         public Map<WorkItem, PolicyContext.WorkItemAction> getWorkItems() {
/* 370 */           Map<WorkItem, PolicyContext.WorkItemAction> result = new HashMap<>(serverData.myWorkItems.getWorkItemsActions().size());
/* 371 */           for (Map.Entry<WorkItem, CheckinWorkItemAction> entry : (Iterable<Map.Entry<WorkItem, CheckinWorkItemAction>>)serverData.myWorkItems.getWorkItemsActions().entrySet()) {
/* 372 */             result
/* 373 */               .put(entry.getKey(), (entry.getValue() == CheckinWorkItemAction.Associate) ? PolicyContext.WorkItemAction.Associate : PolicyContext.WorkItemAction.Resolve);
/*     */           }
/* 375 */           return result;
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public enum Severity {
/* 381 */     ERROR, WARNING, BOTH;
/*     */   }
/*     */   
/*     */   public boolean evaluationEnabled() {
/* 385 */     for (ServerData data : this.myData.values()) {
/* 386 */       for (TeamProjectData teamProjectData : data.myPolicies.values()) {
/* 387 */         if (teamProjectData.myCompatibility.teamExplorer || teamProjectData.myCompatibility.teamprise) return true; 
/*     */       } 
/*     */     } 
/* 390 */     return false;
/*     */   }
/*     */   
/*     */   @Nullable
/*     */   public Pair<String, Severity> getValidationMessage(Severity severity) {
/* 395 */     StringBuilder result = new StringBuilder();
/* 396 */     Severity resultingSeverity = Severity.WARNING;
/*     */     
/* 398 */     boolean checkError = (severity == Severity.ERROR || severity == Severity.BOTH);
/* 399 */     boolean checkWarning = (severity == Severity.WARNING || severity == Severity.BOTH);
/*     */     
/* 401 */     if (!this.myPoliciesEvaluated && checkWarning) {
/* 402 */       if (evaluationEnabled()) {
/* 403 */         result.append("Checkin policies were not evaluated");
/*     */       }
/* 405 */       checkWarning = false;
/*     */     } 
/*     */     
/* 408 */     for (Map.Entry<ServerInfo, ServerData> entry : this.myData.entrySet()) {
/* 409 */       ServerData data = entry.getValue();
/* 410 */       if ((checkError && !data.myEmptyNotes.isEmpty()) || (checkWarning && !data.myPolicyFailures.isEmpty())) {
/* 411 */         if (result.length() > 0) {
/* 412 */           result.append("\n");
/*     */         }
/* 414 */         if (this.myData.size() > 1) {
/* 415 */           result.append(((ServerInfo)entry.getKey()).getPresentableUri()).append("\n");
/*     */         }
/* 417 */         if (checkError && !data.myEmptyNotes.isEmpty()) {
/* 418 */           String message; resultingSeverity = Severity.ERROR;
/*     */           
/* 420 */           if (data.myEmptyNotes.size() > 1) {
/*     */             
/* 422 */             message = MessageFormat.format("Checkin notes ''{0}'' are required to commit", new Object[] { StringUtil.join(ArrayUtilRt.toStringArray(data.myEmptyNotes), "', '") });
/*     */           }
/*     */           else {
/*     */             
/* 426 */             message = MessageFormat.format("Checkin note ''{0}'' is required to commit", new Object[] { data.myEmptyNotes.iterator().next() });
/*     */           } 
/* 428 */           result.append(message);
/*     */         } 
/*     */         
/* 431 */         if (checkWarning && !data.myPolicyFailures.isEmpty()) {
/* 432 */           if (checkError && !data.myEmptyNotes.isEmpty()) {
/* 433 */             result.append("\n");
/*     */           }
/* 435 */           result.append("Checkin policy warnings found");
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 440 */     return (result.length() > 0) ? Pair.create(result.toString(), resultingSeverity) : null;
/*     */   }
/*     */   
/*     */   public void validateNotes() {
/* 444 */     for (ServerData serverData : this.myData.values()) {
/* 445 */       List<String> emptyNotes = new ArrayList<>();
/* 446 */       for (CheckinNote checkinNote : serverData.myCheckinNotes) {
/* 447 */         if (checkinNote.required && StringUtil.isEmptyOrSpaces(checkinNote.value)) {
/* 448 */           emptyNotes.add(checkinNote.name);
/*     */         }
/*     */       } 
/* 451 */       serverData.myEmptyNotes = emptyNotes;
/*     */     } 
/*     */   }
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
/* 502 */   public List<ServerInfo> getServers() { return new ArrayList<>(this.myData.keySet()); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/* 507 */   public Set<String> getProjectPaths(@NotNull ServerInfo server) { return ((ServerData)this.myData.get(server)).myPolicies.keySet(); }
/*     */ 
/*     */ 
/*     */   
/* 511 */   public List<CheckinNote> getCheckinNotes(ServerInfo server) { return Collections.unmodifiableList(((ServerData)this.myData.get(server)).myCheckinNotes); }
/*     */ 
/*     */ 
/*     */   
/* 515 */   public boolean hasEmptyNotes(ServerInfo server) { return !((ServerData)this.myData.get(server)).myEmptyNotes.isEmpty(); }
/*     */ 
/*     */   
/*     */   public boolean hasPolicyFailures(ServerInfo server) {
/* 519 */     ServerData serverData = this.myData.get(server);
/* 520 */     boolean evaluationEnabled = false;
/* 521 */     for (TeamProjectData teamProjectData : serverData.myPolicies.values()) {
/* 522 */       if (teamProjectData.myCompatibility.teamExplorer || teamProjectData.myCompatibility.teamprise) {
/* 523 */         evaluationEnabled = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 527 */     return (evaluationEnabled && (!this.myPoliciesEvaluated || !serverData.myPolicyFailures.isEmpty()));
/*     */   }
/*     */   
/*     */   public List<PolicyFailure> getFailures(ServerInfo server) {
/* 531 */     if (!this.myPoliciesEvaluated) {
/* 532 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 535 */     return Collections.unmodifiableList(((ServerData)this.myData.get(server)).myPolicyFailures);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<PolicyFailure> getAllFailures() {
/* 540 */     if (!this.myPoliciesEvaluated) {
/* 541 */       return Collections.emptyList();
/*     */     }
/*     */     
/* 544 */     List<PolicyFailure> result = new ArrayList<>();
/* 545 */     for (ServerData data : this.myData.values()) {
/* 546 */       result.addAll(data.myPolicyFailures);
/*     */     }
/* 548 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 554 */   public WorkItemsCheckinParameters getWorkItems(ServerInfo server) { return ((ServerData)this.myData.get(server)).myWorkItems; }
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
/*     */   public CheckinParameters createCopy() {
/* 600 */     Map<ServerInfo, ServerData> result = new LinkedHashMap<>(this.myData.size());
/* 601 */     for (Map.Entry<ServerInfo, ServerData> entry : this.myData.entrySet()) {
/* 602 */       ServerData serverData = entry.getValue();
/* 603 */       List<CheckinNote> checkinNotesCopy = new ArrayList<>(serverData.myCheckinNotes.size());
/* 604 */       for (CheckinNote original : serverData.myCheckinNotes) {
/* 605 */         CheckinNote copy = new CheckinNote(original.name, original.required);
/* 606 */         copy.value = original.value;
/* 607 */         checkinNotesCopy.add(copy);
/*     */       } 
/*     */       
/* 610 */       ServerData serverDataCopy = new ServerData(checkinNotesCopy, serverData.myWorkItems.createCopy(), serverData.myFiles, serverData.myPolicies);
/* 611 */       serverDataCopy.myEmptyNotes = new ArrayList<>(serverData.myEmptyNotes);
/* 612 */       serverDataCopy.myPolicyFailures = serverData.myPolicyFailures;
/* 613 */       result.put(entry.getKey(), serverDataCopy);
/*     */     } 
/* 615 */     return new CheckinParameters(this.myPanel, result, this.myPoliciesEvaluated, this.myPoliciesLoadError);
/*     */   }
/*     */ 
/*     */   
/* 619 */   public void setOverrideReason(String value) { this.myOverrideReason = value; }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Pair<String, Map<String, String>> getPolicyOverride(ServerInfo server) {
/* 624 */     if (this.myOverrideReason == null) {
/* 625 */       return null;
/*     */     }
/*     */     
/* 628 */     Map<String, String> failures = new LinkedHashMap<>();
/* 629 */     for (PolicyFailure policyFailure : ((ServerData)this.myData.get(server)).myPolicyFailures) {
/* 630 */       failures.put(policyFailure.getPolicyName(), policyFailure.getMessage());
/*     */     }
/*     */     
/* 633 */     return Pair.create(this.myOverrideReason, failures);
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\checkin\CheckinParameters.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */