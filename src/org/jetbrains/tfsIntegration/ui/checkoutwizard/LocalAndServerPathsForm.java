/*     */ package org.jetbrains.tfsIntegration.ui.checkoutwizard;
/*     */ 
/*     */ import com.intellij.openapi.Disposable;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptor;
/*     */ import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
/*     */ import com.intellij.openapi.ui.ComponentWithBrowseButton;
/*     */ import com.intellij.openapi.ui.TextComponentAccessor;
/*     */ import com.intellij.openapi.ui.TextFieldWithBrowseButton;
/*     */ import com.intellij.openapi.util.Disposer;
/*     */ import com.intellij.ui.DocumentAdapter;
/*     */ import com.intellij.uiDesigner.core.GridConstraints;
/*     */ import com.intellij.uiDesigner.core.GridLayoutManager;
/*     */ import com.intellij.uiDesigner.core.Spacer;
/*     */ import com.intellij.util.EventDispatcher;
/*     */ import com.intellij.util.ui.UIUtil;
/*     */ import java.awt.Component;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Insets;
/*     */ import java.awt.LayoutManager;
/*     */ import java.awt.event.ActionListener;
/*     */ import javax.swing.JComponent;
/*     */ import javax.swing.JLabel;
/*     */ import javax.swing.JPanel;
/*     */ import javax.swing.JTextField;
/*     */ import javax.swing.event.ChangeEvent;
/*     */ import javax.swing.event.ChangeListener;
/*     */ import javax.swing.event.DocumentEvent;
/*     */ import javax.swing.event.DocumentListener;
/*     */ import org.jetbrains.annotations.NotNull;
/*     */ import org.jetbrains.annotations.Nullable;
/*     */ import org.jetbrains.tfsIntegration.core.TFSBundle;
/*     */ import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
/*     */ import org.jetbrains.tfsIntegration.ui.servertree.TfsTreeForm;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocalAndServerPathsForm
/*     */   implements Disposable
/*     */ {
/*     */   private TextFieldWithBrowseButton myLocalPathField;
/*     */   private JPanel myContentPanel;
/*     */   private JLabel myMessageLabel;
/*     */   private TfsTreeForm myServerPathForm;
/*     */   private JLabel myServerPathLabel;
/*     */   private JLabel myLocalPathLabel;
/*     */   private final EventDispatcher<ChangeListener> myEventDispatcher;
/*     */   
/*     */   public LocalAndServerPathsForm() {
/*  49 */      this.myEventDispatcher = EventDispatcher.create(ChangeListener.class);
/*     */ 
/*     */     
/*  52 */     Disposer.register(this, (Disposable)this.myServerPathForm);
/*     */     
/*  54 */     this.myServerPathForm.addListener(new TfsTreeForm.SelectionListener()
/*     */         {
/*     */           public void selectionChanged() {
/*  57 */             ((ChangeListener)LocalAndServerPathsForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*     */           }
/*     */         });
/*     */     
/*  61 */     this.myLocalPathLabel.setLabelFor(this.myLocalPathField.getChildComponent());
/*  62 */     ((JTextField)this.myLocalPathField.getChildComponent()).getDocument().addDocumentListener((DocumentListener)new DocumentAdapter()
/*     */         {
/*     */           protected void textChanged(@NotNull DocumentEvent e) {
/*  65 */               ((ChangeListener)LocalAndServerPathsForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
/*     */           }
/*     */         });
/*     */     
/*  69 */     FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
/*     */ 
/*     */     
/*  72 */     ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> listener = new ComponentWithBrowseButton.BrowseFolderActionListener(TFSBundle.message("choose.local.path.title", new Object[0]), TFSBundle.message("choose.local.path.description", new Object[0]), (ComponentWithBrowseButton)this.myLocalPathField, null, descriptor, TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
/*     */ 
/*     */ 
/*     */     
/*  76 */     this.myServerPathLabel.setLabelFor(this.myServerPathForm.getPreferredFocusedComponent());
/*  77 */     this.myLocalPathField.addActionListener((ActionListener)listener);
/*  78 */     this.myMessageLabel.setIcon(UIUtil.getBalloonWarningIcon());
/*     */   }
/*     */ 
/*     */   
/*  82 */   public void initialize(ServerInfo server, String initialPath) { this.myServerPathForm.initialize(server, initialPath, true, false, null); }
/*     */ 
/*     */ 
/*     */   
/*  86 */   public String getLocalPath() { return this.myLocalPathField.getText(); }
/*     */ 
/*     */ 
/*     */   
/*  90 */   public JPanel getContentPanel() { return this.myContentPanel; }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nullable
/*  95 */   public String getServerPath() { return this.myServerPathForm.getSelectedPath(); }
/*     */ 
/*     */ 
/*     */   
/*  99 */   public void addListener(ChangeListener listener) { this.myEventDispatcher.addListener(listener); }
/*     */ 
/*     */   
/*     */   public void setMessage(String text, boolean error) {
/* 103 */     if (text != null) {
/* 104 */       this.myMessageLabel.setVisible(true);
/* 105 */       this.myMessageLabel.setText(text);
/* 106 */       this.myMessageLabel.setIcon(error ? UIUtil.getBalloonWarningIcon() : TfsTreeForm.EMPTY_ICON);
/*     */     } else {
/*     */       
/* 109 */       this.myMessageLabel.setVisible(false);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/* 114 */   public JComponent getPreferredFocusedComponent() { return this.myServerPathForm.getPreferredFocusedComponent(); }
/*     */   
/*     */   public void dispose() {}
/*     */ }


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\checkoutwizard\LocalAndServerPathsForm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */