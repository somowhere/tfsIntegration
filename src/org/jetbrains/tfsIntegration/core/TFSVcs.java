/*     */ package org.jetbrains.tfsIntegration.core;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.options.Configurable;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.openapi.vcs.AbstractVcs;
/*     */ import com.intellij.openapi.vcs.CheckoutProvider;
/*     */ import com.intellij.openapi.vcs.CommittedChangesProvider;
/*     */ import com.intellij.openapi.vcs.EditFileProvider;
/*     */ import com.intellij.openapi.vcs.FilePath;
/*     */ import com.intellij.openapi.vcs.ProjectLevelVcsManager;
/*     */ import com.intellij.openapi.vcs.VcsConfiguration;
/*     */ import com.intellij.openapi.vcs.VcsKey;
/*     */ import com.intellij.openapi.vcs.VcsShowConfirmationOption;
/*     */ import com.intellij.openapi.vcs.VcsShowSettingOption;
/*     */ import com.intellij.openapi.vcs.VcsVFSListener;
/*     */ import com.intellij.openapi.vcs.annotate.AnnotationProvider;
/*     */ import com.intellij.openapi.vcs.changes.ChangeProvider;
/*     */ import com.intellij.openapi.vcs.checkin.CheckinEnvironment;
/*     */ import com.intellij.openapi.vcs.diff.DiffProvider;
/*     */ import com.intellij.openapi.vcs.history.VcsHistoryProvider;
/*     */ import com.intellij.openapi.vcs.history.VcsRevisionNumber;
/*     */ import com.intellij.openapi.vcs.rollback.RollbackEnvironment;
/*     */ import com.intellij.openapi.vcs.update.UpdateEnvironment;
/*     */ import com.intellij.openapi.vcs.versionBrowser.ChangeBrowserSettings;
/*     */ import com.intellij.openapi.vfs.VirtualFile;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import com.intellij.vcsUtil.VcsUtil;
/*     */ import java.util.List;
/*     */ import javax.swing.JLabel;
/*     */ import org.jetbrains.annotations.NonNls;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsFileUtil;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsRevisionNumber;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.Workstation;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TFSVcs
/*     */   extends AbstractVcs
/*     */ {
/*     */   @NonNls
/*     */   public static final String TFS_NAME = "TFS";
/*     */   
/*     */   public static class CheckinData
/*     */   {
/*     */     public CheckinParameters parameters;
/*     */     JLabel messageLabel;
/*     */   }
/*  59 */   public static final Logger LOG = Logger.getInstance("org.jetbrains.tfsIntegration.core.TFSVcs");
/*  60 */   private static final VcsKey ourKey = createKey("TFS");
/*     */   
/*     */   private VcsVFSListener myFileListener;
/*     */   private final VcsShowConfirmationOption myAddConfirmation;
/*     */   private final VcsShowConfirmationOption myDeleteConfirmation;
/*     */   private final VcsShowSettingOption myCheckoutOptions;
/*     */   private CommittedChangesProvider<TFSChangeList, ChangeBrowserSettings> myCommittedChangesProvider;
/*     */   private VcsHistoryProvider myHistoryProvider;
/*     */   private DiffProvider myDiffProvider;
/*     */   private TFSCheckinEnvironment myCheckinEnvironment;
/*     */   private UpdateEnvironment myUpdateEnvironment;
/*     */   private AnnotationProvider myAnnotationProvider;
/*  72 */   private final List<RevisionChangedListener> myRevisionChangedListeners = ContainerUtil.createLockFreeCopyOnWriteList();
/*  73 */   private final CheckinData myCheckinData = new CheckinData();
/*     */   
/*     */   public TFSVcs(@NotNull Project project) {
/*  76 */     super(project, "TFS");
/*  77 */     ProjectLevelVcsManager vcsManager = ProjectLevelVcsManager.getInstance(project);
/*  78 */     this.myAddConfirmation = vcsManager.getStandardConfirmation(VcsConfiguration.StandardConfirmation.ADD, this);
/*  79 */     this.myDeleteConfirmation = vcsManager.getStandardConfirmation(VcsConfiguration.StandardConfirmation.REMOVE, this);
/*  80 */     this.myCheckoutOptions = vcsManager.getStandardOption(VcsConfiguration.StandardOption.CHECKOUT, this);
/*     */   }
/*     */ 
/*     */   
/*  84 */   public CheckinData getCheckinData() { return this.myCheckinData; }
/*     */ 
/*     */ 
/*     */   
/*  88 */   public static TFSVcs getInstance(Project project) { return (TFSVcs)ProjectLevelVcsManager.getInstance(project).findVcsByName("TFS"); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   @NonNls
/*  95 */   public String getDisplayName() {     return "TFS"; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 100 */   public Configurable getConfigurable() { return new TFSProjectConfigurable(this.myProject); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void activate() {
/* 106 */     this.myFileListener = new TFSFileListener(getProject(), this);
/* 107 */     TfsSdkManager.activate();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 112 */   public void deactivate() { Disposer.dispose((Disposable)this.myFileListener); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 117 */   public ChangeProvider getChangeProvider() { return new TFSChangeProvider(this.myProject); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public TFSCheckinEnvironment createCheckinEnvironment() {
/* 123 */     if (this.myCheckinEnvironment == null) {
/* 124 */       this.myCheckinEnvironment = new TFSCheckinEnvironment(this);
/*     */     }
/* 126 */        return this.myCheckinEnvironment;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 131 */   public RollbackEnvironment createRollbackEnvironment() { return (RollbackEnvironment)new TFSRollbackEnvironment(this.myProject); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 136 */   public boolean fileIsUnderVcs(FilePath filePath) { return isVersionedDirectory(filePath.getVirtualFile()); }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isVersionedDirectory(VirtualFile dir) {
/* 141 */     if (dir == null) {
/* 142 */       return false;
/*     */     }
/* 144 */     return !Workstation.getInstance().findWorkspacesCached(TfsFileUtil.getFilePath(dir), false).isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/* 150 */   public EditFileProvider getEditFileProvider() {      return new TFSEditFileProvider(this.myProject); }
/*     */ 
/*     */ 
/*     */   
/*     */   public UpdateEnvironment createUpdateEnvironment() {
/* 155 */     if (this.myUpdateEnvironment == null) {
/* 156 */       this.myUpdateEnvironment = new TFSUpdateEnvironment(this);
/*     */     }
/* 158 */     return this.myUpdateEnvironment;
/*     */   }
/*     */ 
/*     */   
/*     */   public AnnotationProvider getAnnotationProvider() {
/* 163 */     if (this.myAnnotationProvider == null) {
/* 164 */       this.myAnnotationProvider = new TFSAnnotationProvider(this);
/*     */     }
/* 166 */     return this.myAnnotationProvider;
/*     */   }
/*     */ 
/*     */   
/*     */   public static void assertTrue(boolean condition, @NonNls String message) {
/* 171 */     LOG.assertTrue(condition, message);
/* 172 */     if (!condition) {
/* 173 */       error(message);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static void error(@NonNls String message) {
/* 179 */     LOG.error(message);
/* 180 */     throw new RuntimeException("Assertion failed: " + message);
/*     */   }
/*     */ 
/*     */   
/* 184 */   public static void assertTrue(boolean condition) { assertTrue(condition, ""); }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public CommittedChangesProvider<TFSChangeList, ChangeBrowserSettings> getCommittedChangesProvider() {
/* 190 */     if (this.myCommittedChangesProvider == null) {
/* 191 */       this.myCommittedChangesProvider = (CommittedChangesProvider<TFSChangeList, ChangeBrowserSettings>)new TFSCommittedChangesProvider(this.myProject);
/*     */     }
/* 193 */        return this.myCommittedChangesProvider;
/*     */   }
/*     */ 
/*     */   
/*     */   public VcsHistoryProvider getVcsHistoryProvider() {
/* 198 */     if (this.myHistoryProvider == null) {
/* 199 */       this.myHistoryProvider = new TFSHistoryProvider(this.myProject);
/*     */     }
/* 201 */     return this.myHistoryProvider;
/*     */   }
/*     */ 
/*     */   
/*     */   public DiffProvider getDiffProvider() {
/* 206 */     if (this.myDiffProvider == null) {
/* 207 */       this.myDiffProvider = new TFSDiffProvider(this.myProject);
/*     */     }
/* 209 */     return this.myDiffProvider;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/* 215 */   public VcsRevisionNumber parseRevisionNumber(String revisionNumberString) { return TfsRevisionNumber.tryParse(revisionNumberString); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/* 221 */   public String getRevisionPattern() { return "\\d+"; }
/*     */ 
/*     */   
/*     */   public void fireRevisionChanged() {
/* 225 */     for (RevisionChangedListener listener : this.myRevisionChangedListeners) {
/* 226 */       listener.revisionChanged();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/* 231 */   public void addRevisionChangedListener(RevisionChangedListener listener) { this.myRevisionChangedListeners.add(listener); }
/*     */ 
/*     */ 
/*     */   
/* 235 */   public void removeRevisionChangedListener(RevisionChangedListener listener) { this.myRevisionChangedListeners.remove(listener); }
/*     */ 
/*     */ 
/*     */   
/* 239 */   public static VcsKey getKey() { return ourKey; }
/*     */ 
/*     */   
/*     */   public static boolean isUnderTFS(FilePath path, Project project) {
/* 243 */     AbstractVcs vcs = VcsUtil.getVcsFor(project, path);
/* 244 */     return (vcs != null && "TFS".equals(vcs.getName()));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 249 */   public CheckoutProvider getCheckoutProvider() { return new TFSCheckoutProvider(); }
/*     */   
/*     */   public static interface RevisionChangedListener {
/*     */     void revisionChanged();
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSVcs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */