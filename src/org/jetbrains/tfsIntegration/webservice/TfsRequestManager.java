/*     */ package org.jetbrains.tfsIntegration.webservice;
/*     */ import com.intellij.openapi.application.ApplicationManager;
/*     */ import com.intellij.openapi.diagnostic.Logger;
/*     */ import com.intellij.openapi.progress.ProgressIndicator;
/*     */ import com.intellij.openapi.progress.ProgressManager;
/*     */ import com.intellij.openapi.project.Project;
/*     */ import com.intellij.openapi.util.ClassLoaderUtil;
import com.intellij.openapi.util.Condition;
/*     */ import com.intellij.openapi.util.Ref;
/*     */ import com.intellij.util.WaitForProgressToShow;
/*     */ import com.intellij.util.concurrency.Semaphore;
/*     */ import java.net.URI;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import javax.swing.JComponent;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.config.TfsServerConnectionHelper;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.Credentials;
/*     */ import org.jetbrains.tfsIntegration.core.configuration.TFSConfigurationManager;
/*     */ import org.jetbrains.tfsIntegration.exceptions.AuthCancelledException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.ConnectionFailedException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsException;
/*     */ import org.jetbrains.tfsIntegration.exceptions.TfsExceptionManager;
/*     */ import org.jetbrains.tfsIntegration.exceptions.UserCancelledException;
/*     */ import org.jetbrains.tfsIntegration.ui.TfsLoginDialog;
/*     */ 
/*     */ public class TfsRequestManager {
/*     */   private static final long POLL_TIMEOUT = 200L;
/*     */   
/*     */   public static abstract class Request<T> {
/*     */     private final String myProgressTitle;
/*     */     
/*  35 */     public Request(String progressTitle) { this.myProgressTitle = progressTitle; }
/*     */ 
/*     */     
/*     */     public abstract T execute(Credentials param1Credentials, URI param1URI, @Nullable ProgressIndicator param1ProgressIndicator) throws Exception;
/*     */ 
/*     */     
/*     */     @NotNull
/*  42 */     public String getProgressTitle(Credentials credentials, URI serverUri) {  return this.myProgressTitle; }
/*     */ 
/*     */ 
/*     */     
/*  46 */     public boolean retrieveAuthorizedCredentials() { return true; }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  52 */   private static final Map<URI, TfsRequestManager> ourInstances = new HashMap<>();
/*  53 */   private static final Logger LOG = Logger.getInstance(TfsRequestManager.class.getName());
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private final URI myServerUri;
/*     */   
/*  59 */   private static final ReentrantLock ourShowDialogLock = new ReentrantLock();
/*     */   
/*     */   private final ReentrantLock myRequestLock;
/*     */   
/*     */   private TfsRequestManager(@Nullable URI serverUri) {
/*  64 */     this.myRequestLock = new ReentrantLock();
/*     */ 
/*     */     
/*  67 */     this.myServerUri = serverUri;
/*     */   }
/*     */   
/*     */   public static synchronized TfsRequestManager getInstance(@Nullable URI serverUri) {
/*  71 */     TfsRequestManager result = ourInstances.get(serverUri);
/*  72 */     if (result == null) {
/*  73 */       result = new TfsRequestManager(serverUri);
/*  74 */       ourInstances.put(serverUri, result);
/*     */     } 
/*  76 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T> T executeRequestInBackground(Object projectOrComponent, boolean force, Request<T> request) throws TfsException {
/*  87 */     LOG.assertTrue(!ApplicationManager.getApplication().isDispatchThread());
/*  88 */     LOG.assertTrue((this.myServerUri != null));
/*     */     
/*  90 */     boolean showDialog = shouldShowDialog(force);
/*  91 */     Ref<String> message = new Ref();
/*  92 */     Ref<Credentials> credentials = new Ref(TFSConfigurationManager.getInstance().getCredentials(this.myServerUri));
/*     */     
/*     */     while (true) {
/*  95 */       if (showDialog || !message.isNull()) {
/*     */         try {
/*  97 */           ourShowDialogLock.lock();
/*  98 */           ProgressManager.checkCanceled();
/*  99 */           showDialog = shouldShowDialog(force);
/*     */           
/* 101 */           if (!message.isNull() || showDialog) {
/* 102 */             ProgressIndicator pi = ProgressManager.getInstance().getProgressIndicator();
/* 103 */             if (pi != null) {
/* 104 */               WaitForProgressToShow.execute(pi);
/*     */             }
/*     */             
/* 107 */             Ref<Boolean> ok = new Ref();
/* 108 */             ApplicationManager.getApplication().invokeAndWait(() -> {
/* 109 */                   TfsLoginDialog d; if (message.isNull()) {
/*     */                     try {
/* 111 */                       if (!shouldShowDialog(force)) {
/* 112 */                         ok.set(Boolean.valueOf(true));
/*     */                         
/*     */                         return;
/*     */                       } 
/* 116 */                     } catch (UserCancelledException e) {
/* 117 */                       ok.set(Boolean.valueOf(false));
/*     */                       
/*     */                       return;
/*     */                     } 
/*     */                   }
/* 122 */                   if (projectOrComponent instanceof JComponent) {
/* 123 */                     d = new TfsLoginDialog((JComponent)projectOrComponent, this.myServerUri, (Credentials)credentials.get(), false, null);
/*     */                   } else {
/*     */                     
/* 126 */                     d = new TfsLoginDialog((Project)projectOrComponent, this.myServerUri, (Credentials)credentials.get(), false, null);
/*     */                   } 
/* 128 */                   d.setMessage((String)message.get());
/* 129 */                   if (d.showAndGet()) {
/* 130 */                     credentials.set(d.getCredentials());
/* 131 */                     ok.set(Boolean.valueOf(true));
/*     */                   } else {
/*     */                     
/* 134 */                     ok.set(Boolean.valueOf(false));
/*     */                   } 
/*     */                 });
/*     */             
/* 138 */             if (!((Boolean)ok.get()).booleanValue()) {
/* 139 */               if (!force) {
/* 140 */                 TFSConfigurationManager.getInstance().setAuthCanceled(this.myServerUri, projectOrComponent);
/*     */               }
/* 142 */               throw new AuthCancelledException(this.myServerUri);
/*     */             } 
/*     */           } else {
/*     */             
/* 146 */             credentials.set(TFSConfigurationManager.getInstance().getCredentials(this.myServerUri));
/*     */           } 
/*     */         } finally {
/*     */           
/* 150 */           ourShowDialogLock.unlock();
/*     */         } 
/*     */       }
/* 153 */       LOG.assertTrue(!credentials.isNull());
/*     */       
/* 155 */       try { this.myRequestLock.lock();
/* 156 */         ProgressManager.checkCanceled();
/* 157 */         ProgressIndicator pi = ProgressManager.getInstance().getProgressIndicator();
/* 158 */         T result = executeRequestImpl(this.myServerUri, credentials, request, pi);
/* 159 */         TFSConfigurationManager.getInstance().storeCredentials(this.myServerUri, (Credentials)credentials.get());
/* 160 */         return result; }
/*     */       
/* 162 */       catch (Exception e)
/* 163 */       { TfsException tfsException = TfsExceptionManager.processException(e);
/* 164 */         LOG.warn((Throwable)tfsException);
/* 165 */         if (tfsException instanceof org.jetbrains.tfsIntegration.exceptions.UnauthorizedException)
/* 166 */         { message.set(getMessage(tfsException, ((Credentials)credentials.get()).getType()));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 175 */           this.myRequestLock.unlock(); continue; }  if (!(tfsException instanceof ConnectionFailedException)) TFSConfigurationManager.getInstance().storeCredentials(this.myServerUri, (Credentials)credentials.get());  throw tfsException; } finally { this.myRequestLock.unlock(); }
/*     */     
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private class ExecuteSession<T>
/*     */     implements Runnable
/*     */   {
/*     */     private final Ref<Credentials> myCredentials;
/*     */     
/*     */     private final Object myProjectOrComponent;
/*     */     private final TfsRequestManager.Request<T> myRequest;
/*     */     private final URI myCurrentServerUri;
/*     */     private T myResult;
/*     */     private TfsException myError;
/*     */     
/*     */     ExecuteSession(Credentials credentials, Object projectOrComponent, TfsRequestManager.Request<T> request, URI currentServerUri) {
/* 193 */       this.myCredentials = Ref.create(credentials);
/* 194 */       this.myProjectOrComponent = projectOrComponent;
/* 195 */       this.myRequest = request;
/* 196 */       this.myCurrentServerUri = currentServerUri;
/*     */     }
/*     */ 
/*     */     
/* 200 */     public TfsException getError() { return this.myError; }
/*     */ 
/*     */ 
/*     */     
/* 204 */     public Credentials getCredentials() { return (Credentials)this.myCredentials.get(); }
/*     */ 
/*     */ 
/*     */     
/*     */     public void run() {
/* 209 */       ProgressIndicator pi = ProgressManager.getInstance().getProgressIndicator();
/* 210 */       Semaphore done = new Semaphore();
/*     */       
/* 212 */       pi.setIndeterminate(true);
/* 213 */       done.down();
/*     */       
/* 215 */       ApplicationManager.getApplication().executeOnPooledThread(() -> {
/*     */             try {
/* 217 */               TfsRequestManager.this.myRequestLock.lock();
/* 218 */               this.myResult = TfsRequestManager.executeRequestImpl(this.myCurrentServerUri, this.myCredentials, this.myRequest, pi);
/*     */             }
/* 220 */             catch (Exception e) {
/* 221 */               LOG.warn(e);
/* 222 */               this.myError = TfsExceptionManager.processException(e);
/*     */             } finally {
/*     */               
/* 225 */               TfsRequestManager.this.myRequestLock.unlock();
/* 226 */               done.up();
/*     */             } 
/*     */           }); do {  }
/* 229 */       while (!done.waitFor(200L) && 
/* 230 */         !pi.isCanceled());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean execute() {
/* 240 */       Project project = (this.myProjectOrComponent instanceof Project) ? (Project)this.myProjectOrComponent : null;
/* 241 */       JComponent component = (this.myProjectOrComponent instanceof JComponent) ? (JComponent)this.myProjectOrComponent : null;
/* 242 */       return ProgressManager.getInstance()
/* 243 */         .runProcessWithProgressSynchronously(this, this.myRequest.getProgressTitle((Credentials)this.myCredentials.get(), this.myCurrentServerUri), true, project, component);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 249 */   public static <T> T executeRequest(URI serverUri, Object projectOrComponent, Request<T> request) throws TfsException { return executeRequest(serverUri, projectOrComponent, false, request); }
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T executeRequest(URI serverUri, Object projectOrComponent, boolean force, Request<T> request) throws TfsException {
/* 254 */     if (ApplicationManager.getApplication().isDispatchThread()) {
/* 255 */       return getInstance(serverUri).executeRequestInForeground(projectOrComponent, false, null, force, request);
/*     */     }
/*     */     
/* 258 */     return getInstance(serverUri).executeRequestInBackground(projectOrComponent, force, request);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 268 */   public <T> T executeRequestInForeground(Object projectOrComponent, boolean reportErrorsInDialog, @Nullable Credentials overrideCredentials, boolean force, Request<T> request) throws TfsException { return executeRequestInForeground(projectOrComponent, request, null, reportErrorsInDialog, overrideCredentials, force); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private <T> T executeRequestInForeground(Object projectOrComponent, Request<T> request, @Nullable String errorMessage, boolean reportErrorsInDialog, @Nullable Credentials overrideCredentials, boolean force) throws TfsException {
/* 278 */     LOG.assertTrue(ApplicationManager.getApplication().isDispatchThread());
/*     */     
/* 280 */     Ref<T> result = new Ref();
/* 281 */     Ref<TfsException> fatalError = new Ref();
/* 282 */     if (errorMessage != null || (overrideCredentials == null && shouldShowDialog(force))) {
/*     */       TfsLoginDialog d;
/*     */ 
/*     */       
/* 286 */       Ref<Credentials> credentials = new Ref((overrideCredentials != null) ? overrideCredentials : ((this.myServerUri != null) ? TFSConfigurationManager.getInstance().getCredentials(this.myServerUri) : null));
/*     */ 
/*     */       
/* 289 */       Condition<TfsLoginDialog> condition = dialog -> {
/*     */           
/* 291 */           ExecuteSession<T> session = new ExecuteSession<>(dialog.getCredentials(), dialog.getContentPane(), request, dialog.getUri());
/* 292 */           if (!session.execute()) {
/* 293 */             return false;
/*     */           }
/*     */           
/* 296 */           TfsException error = session.getError();
/* 297 */           if (error != null) {
/* 298 */             if (error instanceof org.jetbrains.tfsIntegration.exceptions.UnauthorizedException || this.myServerUri == null || reportErrorsInDialog) {
/*     */               
/* 300 */               dialog.setMessage(getMessage(error, dialog.getCredentials().getType()));
/* 301 */               return false;
/*     */             } 
/*     */             
/* 304 */             fatalError.set(error);
/* 305 */             if (!(error instanceof ConnectionFailedException))
/*     */             {
/* 307 */               TFSConfigurationManager.getInstance().storeCredentials(dialog.getUri(), session.getCredentials());
/*     */             }
/*     */           }
/*     */           else {
/*     */             
/* 312 */             TFSConfigurationManager.getInstance().storeCredentials(dialog.getUri(), session.getCredentials());
/* 313 */             result.set(session.myResult);
/*     */           } 
/* 315 */           return true;
/*     */         };
/*     */ 
/*     */       
/* 319 */       if (projectOrComponent instanceof JComponent) {
/* 320 */         d = new TfsLoginDialog((JComponent)projectOrComponent, this.myServerUri, (Credentials)credentials.get(), (this.myServerUri == null), condition);
/*     */       } else {
/*     */         
/* 323 */         d = new TfsLoginDialog((Project)projectOrComponent, this.myServerUri, (Credentials)credentials.get(), (this.myServerUri == null), condition);
/*     */       } 
/*     */       
/* 326 */       if (errorMessage != null) {
/* 327 */         d.setMessage(errorMessage);
/*     */       }
/* 329 */       if (d.showAndGet()) {
/* 330 */         if (fatalError.isNull()) {
/* 331 */           return (T)result.get();
/*     */         }
/*     */         
/* 334 */         throw (TfsException)fatalError.get();
/*     */       } 
/*     */ 
/*     */       
/* 338 */       if (!force && this.myServerUri != null) {
/* 339 */         TFSConfigurationManager.getInstance().setAuthCanceled(this.myServerUri, projectOrComponent);
/*     */       }
/* 341 */       throw new AuthCancelledException(this.myServerUri);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 346 */     LOG.assertTrue((this.myServerUri != null));
/*     */ 
/*     */     
/* 349 */     Credentials credentials = (overrideCredentials != null) ? overrideCredentials : TFSConfigurationManager.getInstance().getCredentials(this.myServerUri);
/* 350 */     ExecuteSession<T> session = new ExecuteSession<>(credentials, projectOrComponent, request, this.myServerUri);
/* 351 */     if (!session.execute()) {
/* 352 */       throw new UserCancelledException();
/*     */     }
/*     */     
/* 355 */     TfsException error = session.getError();
/* 356 */     if (error instanceof org.jetbrains.tfsIntegration.exceptions.UnauthorizedException && credentials != null) {
/* 357 */       return executeRequestInForeground(projectOrComponent, request, getMessage(error, credentials.getType()), reportErrorsInDialog, overrideCredentials, force);
/*     */     }
/*     */ 
/*     */     
/* 361 */     TFSConfigurationManager.getInstance().storeCredentials(this.myServerUri, session.getCredentials());
/*     */     
/* 363 */     if (error == null) {
/* 364 */       return session.myResult;
/*     */     }
/*     */     
/* 367 */     throw error;
/*     */   }
/*     */ 
/*     */   
/*     */   private static String getMessage(TfsException error, Credentials.Type type) {
/* 372 */     if (error instanceof ConnectionFailedException && ((ConnectionFailedException)error)
/* 373 */       .getHttpStatusCode() == 302) {
/* 374 */       if (type == Credentials.Type.Alternate) {
/* 375 */         return TFSBundle.message("unauthorized", new Object[0]);
/*     */       }
/*     */       
/* 378 */       return TFSBundle.message("consider.using.alternate.credentials", new Object[] { error.getMessage() });
/*     */     } 
/*     */     
/* 381 */     return error.getMessage();
/*     */   }
/*     */   
/*     */   private boolean shouldShowDialog(boolean force) throws UserCancelledException {
/* 385 */     if (this.myServerUri == null) {
/* 386 */       return true;
/*     */     }
/*     */     
/* 389 */     if (!force && TFSConfigurationManager.getInstance().isAuthCanceled(this.myServerUri)) {
/* 390 */       throw new AuthCancelledException(this.myServerUri);
/*     */     }
/*     */     
/* 393 */     return shouldShowLoginDialog(this.myServerUri);
/*     */   }
/*     */   
/*     */   public static boolean shouldShowLoginDialog(URI serverUri) {
/* 397 */     Credentials credentials = TFSConfigurationManager.getInstance().getCredentials(serverUri);
/* 398 */     return (credentials == null || credentials
/* 399 */       .shouldShowLoginDialog() || 
/* 400 */       TfsLoginDialog.shouldPromptForProxyPassword(true));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <T> T executeRequestImpl(URI serverUri, Ref<Credentials> credentialsRef, Request<T> request, ProgressIndicator pi) throws Exception {
/* 407 */     return (T) ClassLoaderUtil.computeWithClassLoader(TfsRequestManager.class.getClassLoader(), () -> {
/* 408 */           Credentials credentials = (Credentials)credentialsRef.get();
/*     */ 
/*     */           
/* 411 */           boolean needsAuthentication = (credentials == null || (request.retrieveAuthorizedCredentials() && (credentials.getUserName().length() == 0 || credentials.getDomain().length() == 0)));
/* 412 */           if (needsAuthentication) {
/*     */             
/* 414 */             TfsServerConnectionHelper.ServerDescriptor descriptor = TfsServerConnectionHelper.connect(serverUri, (Credentials)credentialsRef.get(), true, pi);
/* 415 */             credentialsRef.set(descriptor.authorizedCredentials);
/*     */           } 
/* 417 */           return request.execute((Credentials)credentialsRef.get(), serverUri, pi);
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegration\webservice\TfsRequestManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */