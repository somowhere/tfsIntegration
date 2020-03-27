//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.util.EventDispatcher;
import com.intellij.util.ui.AbstractTableCellEditor;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.LocalPathCellEditor;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.ValidatingTableEditor;
import com.intellij.util.ui.ValidatingTableEditor.Fix;
import com.intellij.util.ui.ValidatingTableEditor.RowHeightProvider;
import com.intellij.vcsUtil.VcsUtil;
import java.awt.BorderLayout;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.table.TableCellEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.TFSBundle;
import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;
import org.jetbrains.tfsIntegration.core.tfs.WorkingFolderInfo;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.Workstation;
import org.jetbrains.tfsIntegration.core.tfs.WorkingFolderInfo.Status;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo.Location;

public class WorkspaceForm {
    private JTextField myNameField;
    private JLabel myServerField;
    private JLabel myOwnerField;
    private JLabel myComputerField;
    private JTextArea myCommentField;
    private JPanel myContentPane;
    private JPanel myTableWrapper;
    private ValidatingTableEditor<WorkingFolderInfo> myTable;
    private JLabel myMessageLabel;
    private JLabel myWorkingFoldrersLabel;
    private ComboBox myLocationField;
    private ServerInfo myServer;
    private final Project myProject;
    @Nullable
    private String myWorkingFolderValidationMessage;
    private final EventDispatcher<ChangeListener> myEventDispatcher;
    private static final ColumnInfo<WorkingFolderInfo, Object> STATUS_COLUMN = new ColumnInfo<WorkingFolderInfo, Object>(TFSBundle.message("working.folder.status.column", new Object[0])) {
        public Object valueOf(WorkingFolderInfo item) {
            return item.getStatus();
        }

        public void setValue(WorkingFolderInfo item, Object value) {
            item.setStatus((Status)value);
        }

        public boolean isCellEditable(WorkingFolderInfo workingFolderInfo) {
            return true;
        }

        public int getWidth(JTable table) {
            return 80;
        }

        public TableCellEditor getEditor(WorkingFolderInfo o) {
            return new AbstractTableCellEditor() {
                private ComboBox myCombo;

                public Object getCellEditorValue() {
                    return this.myCombo.getSelectedItem();
                }

                public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                    ComboBoxModel model = new EnumComboBoxModel(Status.class);
                    model.setSelectedItem(value);
                    this.myCombo = new ComboBox(model, getWidth(table));
                    return this.myCombo;
                }
            };
        }
    };
    private final ColumnInfo<WorkingFolderInfo, String> SERVER_PATH_COLUMN;

    private void createUIComponents() {
        this.myTable = new ValidatingTableEditor<WorkingFolderInfo>() {
            protected WorkingFolderInfo cloneOf(WorkingFolderInfo item) {
                return item.getCopy();
            }

            protected WorkingFolderInfo createItem() {
                String path = WorkspaceForm.this.myProject.isDefault() ? "" : WorkspaceForm.this.myProject.getBasePath();
                return new WorkingFolderInfo(VcsUtil.getFilePath(path));
            }

            @Nullable
            protected String validate(WorkingFolderInfo item) {
                if (StringUtil.isEmpty(item.getLocalPath().getPath())) {
                    return TFSBundle.message("local.path.is.empty", new Object[0]);
                } else if (StringUtil.isEmpty(item.getServerPath())) {
                    return TFSBundle.message("server.path.is.empty", new Object[0]);
                } else {
                    return !item.getServerPath().startsWith("$/") ? TFSBundle.message("server.path.is.invalid", new Object[0]) : null;
                }
            }

            protected void displayMessageAndFix(@Nullable Pair<String, Fix> messageAndFix) {
                WorkspaceForm.this.myWorkingFolderValidationMessage = (String)Pair.getFirst(messageAndFix);
                ((ChangeListener)WorkspaceForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(this));
            }
        };
        this.myTable.hideMessageLabel();
        this.myTable.setColumnReorderingAllowed(false);
        this.myTableWrapper = new JPanel(new BorderLayout());
        this.myTableWrapper.add(this.myTable.getContentPane());
    }

    private void setupLocations() {
        Location[] var1 = Location.values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Location location = var1[var3];
            this.myLocationField.addItem(location);
        }

    }

    private WorkspaceForm(Project project) {
        this.myEventDispatcher = EventDispatcher.create(ChangeListener.class);
        this.SERVER_PATH_COLUMN = new ColumnInfo<WorkingFolderInfo, String>(TFSBundle.message("working.folder.server.path.column", new Object[0])) {
            public String valueOf(WorkingFolderInfo item) {
                return item.getServerPath();
            }

            public void setValue(WorkingFolderInfo item, String value) {
                item.setServerPath(value);
            }

            public boolean isCellEditable(WorkingFolderInfo item) {
                return true;
            }

            public TableCellEditor getEditor(WorkingFolderInfo item) {
                return new ServerPathCellEditor(TFSBundle.message("choose.server.path.dialog.title", new Object[0]), WorkspaceForm.this.myProject, WorkspaceForm.this.myServer);
            }
        };
        this.myProject = project;
        this.myWorkingFoldrersLabel.setLabelFor(this.myTable.getPreferredFocusedComponent());
        DocumentAdapter listener = new DocumentAdapter() {
            protected void textChanged(@NotNull DocumentEvent e) {
                ((ChangeListener)WorkspaceForm.this.myEventDispatcher.getMulticaster()).stateChanged(new ChangeEvent(e));
            }
        };
        this.myNameField.getDocument().addDocumentListener(listener);
        this.myCommentField.getDocument().addDocumentListener(listener);
        this.myMessageLabel.setIcon(UIUtil.getBalloonWarningIcon());
        this.setupLocations();
    }

    public WorkspaceForm(Project project, @NotNull ServerInfo server) {
        this(project);
        this.myServer = server;
        this.myServerField.setText(this.myServer.getPresentableUri());
        this.myOwnerField.setText(this.myServer.getQualifiedUsername());
        this.myComputerField.setText(Workstation.getComputerName());
        this.myTable.setModel(new ColumnInfo[]{STATUS_COLUMN, new WorkspaceForm.LocalPathColumn(), this.SERVER_PATH_COLUMN}, new ArrayList());
    }

    public WorkspaceForm(Project project, @NotNull WorkspaceInfo workspace) {

        this(project, workspace.getServer());
        this.myNameField.setText(workspace.getName());
        this.myLocationField.setSelectedItem(workspace.getLocation());
        this.myCommentField.setText(workspace.getComment());
        this.myTable.setModel(new ColumnInfo[]{STATUS_COLUMN, new WorkspaceForm.LocalPathColumn(), this.SERVER_PATH_COLUMN}, new ArrayList(workspace.getWorkingFoldersCached()));
    }

    public JPanel getContentPane() {
        return this.myContentPane;
    }

    public String getWorkspaceName() {
        return this.myNameField.getText();
    }

    @NotNull
    public Location getWorkspaceLocation() {
        Location var10000 = (Location)this.myLocationField.getSelectedItem();

        return var10000;
    }

    public String getWorkspaceComment() {
        return this.myCommentField.getText();
    }

    public List<WorkingFolderInfo> getWorkingFolders() {
        return this.myTable.getItems();
    }

    public void addListener(ChangeListener listener) {
        this.myEventDispatcher.addListener(listener);
    }

    public void setErrorMessage(@Nullable String message) {
        this.myMessageLabel.setText(message);
        this.myMessageLabel.setVisible(message != null);
    }

    public JComponent getPreferredFocusedComponent() {
        return this.myNameField;
    }

    @Nullable
    public String validateWorkingFolders() {
        return this.myWorkingFolderValidationMessage;
    }

    private class LocalPathColumn extends ColumnInfo<WorkingFolderInfo, String> implements RowHeightProvider {
        LocalPathColumn() {
            super(TFSBundle.message("working.folder.local.path.column", new Object[0]));
        }

        public String valueOf(WorkingFolderInfo item) {
            return item.getLocalPath().getPresentableUrl();
        }

        public boolean isCellEditable(WorkingFolderInfo workingFolderInfo) {
            return true;
        }

        public void setValue(WorkingFolderInfo item, String value) {
            item.setLocalPath(VcsUtil.getFilePath(value));
        }

        public TableCellEditor getEditor(WorkingFolderInfo item) {
            return new LocalPathCellEditor(TFSBundle.message("select.local.path.title", new Object[0]), WorkspaceForm.this.myProject);
        }

        public int getRowHeight() {
            return (new JTextField()).getPreferredSize().height + 1;
        }
    }
}
