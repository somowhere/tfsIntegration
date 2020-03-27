/*     */ package org.jetbrains.tfsIntegration.core.tfs;
/*     */ 
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.ui.Messages;
/*     */ import com.intellij.openapi.util.Ref;
/*     */ import com.intellij.openapi.vcs.AbstractVcsHelper;
/*     */ import com.intellij.openapi.vcs.VcsException;
/*     */ import com.intellij.util.containers.ContainerUtil;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSProgressUtil;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.UserCancelledException;
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
/*     */ public class TfsExecutionUtil
/*     */ {
/*     */   public static class ResultWithErrors<T>
/*     */   {
/*     */     public final List<VcsException> errors;
/*     */     @Nullable
/*     */     public final T result;
/*     */     @NotNull
/*     */     private final Project project;
/*     */     public final boolean cancelled;
/*     */     
/*     */     private ResultWithErrors(List<VcsException> errors, T result, Project project, boolean cancelled) {
/*  47 */       this.errors = errors;
/*  48 */       this.result = result;
/*  49 */       this.project = project;
/*  50 */       this.cancelled = cancelled;
/*     */     }
/*     */     
/*     */     public void throwIfErrors() throws VcsException {
/*  54 */       if (this.errors.isEmpty()) {
/*     */         return;
/*     */       }
/*     */       
/*  58 */       if (this.errors.size() == 1) {
/*  59 */         throw (VcsException)this.errors.iterator().next();
/*     */       }
/*     */       
/*  62 */       Collection<String> messages = new ArrayList<>(this.errors.size());
/*  63 */       for (VcsException error : this.errors) {
/*  64 */         messages.add(error.getMessage());
/*     */       }
/*  66 */       throw new VcsException(messages);
/*     */     }
/*     */     
/*     */     public boolean showTabIfErrors() {
/*  70 */       if (this.errors.isEmpty()) {
/*  71 */         return false;
/*     */       }
/*  73 */       AbstractVcsHelper.getInstance(this.project).showErrors(this.errors, "TFS");
/*  74 */       return true;
/*     */     }
/*     */     
/*     */     public boolean showDialogIfErrors(String title, String prefix) {
/*  78 */       if (this.errors.isEmpty()) {
/*  79 */         return false;
/*     */       }
/*  81 */       StringBuilder errorMessage = new StringBuilder(prefix + ":\n\n");
/*  82 */       for (VcsException e : this.errors) {
/*  83 */         errorMessage.append(e.getMessage()).append("\n");
/*     */       }
/*  85 */       Messages.showErrorDialog(this.project, errorMessage.toString(), title);
/*  86 */       return true;
/*     */     }
/*     */   }
/*     */   
/*     */   public static class ResultWithError<T>
/*     */   {
/*     */     @Nullable
/*     */     public final VcsException error;
/*     */     @Nullable
/*     */     public final T result;
/*     */     
/*     */     private ResultWithError(@Nullable VcsException error, @Nullable T result, Project project, boolean cancelled) {
/*  98 */       this.error = error;
/*  99 */       this.result = result;
/* 100 */       this.project = project;
/* 101 */       this.cancelled = cancelled;
/*     */     } @NotNull
/*     */     private final Project project; public final boolean cancelled;
/*     */     public void throwIfError() throws VcsException {
/* 105 */       if (this.error != null) {
/* 106 */         throw new VcsException((Throwable)this.error);
/*     */       }
/*     */     }
/*     */     
/*     */     public boolean showTabIfError() {
/* 111 */       if (this.error != null) {
/* 112 */         AbstractVcsHelper.getInstance(this.project).showError(this.error, "TFS");
/* 113 */         return true;
/*     */       } 
/* 115 */       return false;
/*     */     }
/*     */     
/*     */     public boolean showDialogIfError(String title) {
/* 119 */       if (this.error == null) {
/* 120 */         return false;
/*     */       }
/* 122 */       Messages.showErrorDialog(this.project, this.error.getMessage(), title);
/* 123 */       return true;
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
/*     */   public static <T> ResultWithErrors<T> executeInBackground(String progressText, Project project, ProcessWithErrors<? extends T> process) {
/*     */     boolean completed;
/* 147 */     Ref<T> result = new Ref();
/* 148 */     List<VcsException> errors = new ArrayList<>();
/* 149 */     Ref<Boolean> explicitlyCancelled = new Ref();
/* 150 */     Runnable runnable = () -> {
/* 151 */         TFSProgressUtil.setIndeterminate(ProgressManager.getInstance().getProgressIndicator(), true);
/*     */         try {
/* 153 */           result.set(process.run(errors));
/*     */         }
/* 155 */         catch (UserCancelledException e) {
/* 156 */           explicitlyCancelled.set(Boolean.valueOf(true));
/*     */         }
/* 158 */         catch (TfsException e) {
/* 159 */           errors.add(new VcsException(e.getMessage(), (Throwable)e));
/*     */         }
/* 161 */         catch (VcsException e) {
/* 162 */           errors.add(e);
/*     */         } 
/*     */       };
/*     */ 
/*     */     
/* 167 */     if (ApplicationManager.getApplication().isDispatchThread()) {
/* 168 */       completed = ProgressManager.getInstance().runProcessWithProgressSynchronously(runnable, progressText, true, project);
/*     */     } else {
/*     */       
/* 171 */       runnable.run();
/* 172 */       completed = true;
/*     */     } 
/*     */     
/* 175 */     return new ResultWithErrors<>(errors, result.get(), project, (!completed || (
/* 176 */         !explicitlyCancelled.isNull() && ((Boolean)explicitlyCancelled.get()).booleanValue())));
/*     */   }
/*     */   
/*     */   public static ResultWithErrors<Void> executeInBackground(String progressText, Project project, final VoidProcessWithErrors process) {
/* 180 */     return executeInBackground(progressText, project, new ProcessWithErrors<Void>()
/*     */         {
/*     */           public Void run(Collection<VcsException> errorsHolder) throws TfsException, VcsException {
/* 183 */             process.run(errorsHolder);
/* 184 */             return null;
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public static <T> ResultWithError<T> executeInBackground(String progressText, Project project, final Process<? extends T> process) {
/* 190 */     ResultWithErrors<T> result = executeInBackground(progressText, project, new ProcessWithErrors<T>()
/*     */         {
/*     */           public T run(Collection<VcsException> errorsHolder) throws TfsException, VcsException {
/* 193 */             return process.run();
/*     */           }
/*     */         });
/* 196 */     assert result.errors.size() < 2;
/* 197 */     return new ResultWithError<>((VcsException)ContainerUtil.getFirstItem(result.errors, null), result.result, project, result.cancelled);
/*     */   }
/*     */   
/*     */   public static ResultWithError<Void> executeInBackground(String progressText, Project project, final VoidProcess process) {
/* 201 */     return executeInBackground(progressText, project, new Process<Void>()
/*     */         {
/*     */           public Void run() throws TfsException, VcsException {
/* 204 */             process.run();
/* 205 */             return null;
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public static interface VoidProcess {
/*     */     void run() throws TfsException, VcsException;
/*     */   }
/*     */   
/*     */   public static interface Process<T> {
/*     */     @Nullable
/*     */     T run() throws TfsException, VcsException;
/*     */   }
/*     */   
/*     */   public static interface VoidProcessWithErrors {
/*     */     void run(Collection<VcsException> param1Collection) throws TfsException, VcsException;
/*     */   }
/*     */   
/*     */   public static interface ProcessWithErrors<T> {
/*     */     @Nullable
/*     */     T run(Collection<VcsException> param1Collection) throws TfsException, VcsException;
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\core\tfs\TfsExecutionUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */