//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui.servertree;

import com.intellij.ide.DataManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.DataKey;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.PopupHandler;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.EventDispatcher;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.ui.UIUtil;
import java.util.EventListener;
import java.util.Set;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;

public class TfsTreeForm implements Disposable, DataProvider {
    private boolean myCanCreateVirtualFolders;
    public static final DataKey<TfsTreeForm> KEY = DataKey.create("TfsTreeForm");
    public static final String POPUP_ACTION_GROUP = "TfsTreePopupMenu";
    public static final Icon EMPTY_ICON = EmptyIcon.create(0, UIUtil.getBalloonWarningIcon().getIconHeight());
    private JComponent myContentPane;
    private Tree myTree;
    private JTextField myPathField;
    private JLabel myMessageLabel;
    private JPanel myMessagePanel;
    private TfsTreeBuilder myTreeBuider;
    private final EventDispatcher<TfsTreeForm.SelectionListener> myEventDispatcher;
    private TfsTreeForm.SelectedItem mySelectedItem;

    public TfsTreeForm() {
        this.myEventDispatcher = EventDispatcher.create(TfsTreeForm.SelectionListener.class);
        DataManager.registerDataProvider(this.myTree, this);
        new TreeSpeedSearch(this.myTree);
        this.myTree.getSelectionModel().setSelectionMode(1);
        this.myTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TfsTreeForm.this.mySelectedItem = TfsTreeForm.this.doGetSelectedItem();
                TfsTreeForm.this.myPathField.setText(TfsTreeForm.this.mySelectedItem != null ? TfsTreeForm.this.mySelectedItem.path : null);
                ((TfsTreeForm.SelectionListener)TfsTreeForm.this.myEventDispatcher.getMulticaster()).selectionChanged();
            }
        });
        PopupHandler.installPopupHandler(this.myTree, "TfsTreePopupMenu", "RemoteHostDialogPopup");
        this.setMessage((String)null, false);
    }

    @Nullable
    public TfsTreeForm.SelectedItem getSelectedItem() {
        return this.mySelectedItem;
    }

    @Nullable
    public String getSelectedPath() {
        return this.mySelectedItem != null ? this.mySelectedItem.path : null;
    }

    @Nullable
    private TfsTreeForm.SelectedItem doGetSelectedItem() {
        Set<Object> selection = this.myTreeBuider.getSelectedElements();
        if (selection.isEmpty()) {
            return null;
        } else {
            Object o = selection.iterator().next();
            return o instanceof TfsTreeNode ? new TfsTreeForm.SelectedItem((TfsTreeNode)o) : null;
        }
    }

    public void initialize(@NotNull ServerInfo server, @Nullable String initialSelection, boolean foldersOnly, boolean canCreateVirtualFolders, @Nullable Condition<String> pathFilter) {

        this.myCanCreateVirtualFolders = canCreateVirtualFolders;
        TfsTreeNode root = new TfsTreeNode(this.myTree, server, foldersOnly, pathFilter);
        this.myTreeBuider = TfsTreeBuilder.createInstance(root, this.myTree);
        Disposer.register(this, this.myTreeBuider);
        TfsTreeNode selection = root.createForSelection(initialSelection);
        if (selection != null) {
            this.myTreeBuider.select(selection);
        }

    }

    public void addListener(TfsTreeForm.SelectionListener selectionListener) {
        this.myEventDispatcher.addListener(selectionListener, this);
    }

    public JComponent getContentPane() {
        return this.myContentPane;
    }

    public JComponent getPreferredFocusedComponent() {
        return this.myTree;
    }

    public void dispose() {
    }

    public Object getData(@NotNull @NonNls String dataId) {

        return KEY.is(dataId) ? this : null;
    }

    public void createVirtualFolder(String folderName) {
        Set<Object> selection = this.myTreeBuider.getSelectedElements();
        if (!selection.isEmpty()) {
            Object o = selection.iterator().next();
            if (o instanceof TfsTreeNode) {
                TfsTreeNode treeNode = (TfsTreeNode)o;
                TfsTreeNode child = treeNode.createVirtualSubfolder(folderName);
                this.myTreeBuider.queueUpdateFrom(treeNode, true).doWhenDone(() -> {
                    this.myTreeBuider.select(child);
                });
            }
        }
    }

    public boolean canCreateVirtualFolders() {
        return this.myCanCreateVirtualFolders;
    }

    public void setMessage(String text, boolean error) {
        if (text != null) {
            this.myMessagePanel.setVisible(true);
            this.myMessageLabel.setText(text);
            this.myMessageLabel.setIcon(error ? UIUtil.getBalloonWarningIcon() : EMPTY_ICON);
        } else {
            this.myMessagePanel.setVisible(false);
        }

    }

    public static class SelectedItem {
        public final String path;
        public final boolean isDirectory;

        public SelectedItem(String path, boolean idDirectory) {
            this.path = path;
            this.isDirectory = idDirectory;
        }

        public SelectedItem(TfsTreeNode treeNode) {
            this(treeNode.getPath(), treeNode.isDirectory());
        }
    }

    public interface SelectionListener extends EventListener {
        void selectionChanged();
    }
}
