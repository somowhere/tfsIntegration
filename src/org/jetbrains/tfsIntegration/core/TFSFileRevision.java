/*     */ package org.jetbrains.tfsIntegration.core;
/*     */ 
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.vcs.RepositoryLocation;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.openapi.vcs.history.VcsFileRevision;
/*     */ import com.intellij.openapi.vcs.history.VcsRevisionNumber;
/*     */ import java.io.IOException;
/*     */ import java.util.Date;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.revision.TFSContentRevision;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.TfsRevisionNumber;
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
/*     */ public class TFSFileRevision
/*     */   implements VcsFileRevision
/*     */ {
/*     */   private final Project myProject;
/*     */   private final Date myDate;
/*     */   @Nullable
/*     */   private byte[] myContent;
/*     */   private final String myCommitMessage;
/*     */   private final String myAuthor;
/*     */   private final int myItemId;
/*     */   private final int myChangeset;
/*     */   private final WorkspaceInfo myWorkspace;
/*     */   
/*     */   public TFSFileRevision(Project project, WorkspaceInfo workspace, int itemId, Date date, String commitMessage, String author, int changeset) {
/*  51 */     this.myProject = project;
/*  52 */     this.myWorkspace = workspace;
/*  53 */     this.myDate = date;
/*  54 */     this.myCommitMessage = commitMessage;
/*  55 */     this.myAuthor = author;
/*  56 */     this.myChangeset = changeset;
/*  57 */     this.myItemId = itemId;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*  63 */   public VcsRevisionNumber.Int getRevisionNumber() {    return (VcsRevisionNumber.Int)new TfsRevisionNumber(this.myChangeset, this.myItemId); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  68 */   public String getBranchName() { return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   public Date getRevisionDate() { return this.myDate; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  79 */   public RepositoryLocation getChangedRepositoryPath() { return null; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   public String getAuthor() { return this.myAuthor; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   public String getCommitMessage() { return this.myCommitMessage; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   public byte[] loadContent() throws IOException, VcsException { return this.myContent = createContentRevision().getContentAsBytes(); }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/* 100 */   public byte[] getContent() throws IOException, VcsException { return this.myContent; }
/*     */ 
/*     */   
/*     */   public TFSContentRevision createContentRevision() throws VcsException {
/*     */     try {
/* 105 */       return TFSContentRevision.create(this.myProject, this.myWorkspace, this.myChangeset, this.myItemId);
/*     */     }
/* 107 */     catch (TfsException e) {
/* 108 */       throw new VcsException("Cannot get revision content", (Throwable)e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\TFSFileRevision.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */