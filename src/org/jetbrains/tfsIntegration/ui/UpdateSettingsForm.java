//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.IdeFocusManager;
import com.intellij.ui.CollectionListModel;
import java.awt.Component;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.TFSProjectConfiguration;
import org.jetbrains.tfsIntegration.core.tfs.WorkspaceInfo;
import org.jetbrains.tfsIntegration.core.tfs.version.LatestVersionSpec;
import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;

public class UpdateSettingsForm {
    private final Map<WorkspaceInfo, UpdateSettingsForm.WorkspaceSettings> myWorkspaceSettings;
    private JPanel myPanel;
    private JCheckBox myRecursiveBox;
    private JList myWorkspacesList;
    private SelectRevisionForm mySelectRevisionForm;
    private JPanel myWorkspaceSettingsPanel;
    private WorkspaceInfo mySelectedWorkspace;

    public UpdateSettingsForm(final Project project, final String title, Map<WorkspaceInfo, UpdateSettingsForm.WorkspaceSettings> workspaceSettings) {
        this.myWorkspaceSettings = workspaceSettings;
        Map var10002 = this.myWorkspaceSettings;
        List<WorkspaceInfo> workspaces = new ArrayList(var10002.keySet());
        Collections.sort(workspaces, (o1, o2) -> {
            return o1.getName().compareTo(o2.getName());
        });
        this.myWorkspacesList.setModel(new CollectionListModel(workspaces));
        this.myWorkspacesList.getSelectionModel().setSelectionMode(0);
        this.myWorkspacesList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                WorkspaceInfo workspace = (WorkspaceInfo)value;
                String label = MessageFormat.format("{0} [{1}]", workspace.getName(), workspace.getServer().getPresentableUri());
                this.setText(label);
                return c;
            }
        });
        this.myWorkspacesList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                try {
                    UpdateSettingsForm.this.applyCurrentValue();
                } catch (ConfigurationException var3) {
                    Messages.showErrorDialog(project, var3.getMessage(), title);
                }

                UpdateSettingsForm.this.mySelectedWorkspace = (WorkspaceInfo)UpdateSettingsForm.this.myWorkspacesList.getSelectedValue();
                if (UpdateSettingsForm.this.mySelectedWorkspace != null) {
                    UpdateSettingsForm.WorkspaceSettings workspaceSettings = (UpdateSettingsForm.WorkspaceSettings)UpdateSettingsForm.this.myWorkspaceSettings.get(UpdateSettingsForm.this.mySelectedWorkspace);
                    UpdateSettingsForm.this.mySelectRevisionForm.init(project, UpdateSettingsForm.this.mySelectedWorkspace, workspaceSettings.serverPath, workspaceSettings.isDirectory);
                    UpdateSettingsForm.this.mySelectRevisionForm.setVersionSpec(workspaceSettings.version);
                } else {
                    UpdateSettingsForm.this.mySelectRevisionForm.disable();
                }

            }
        });
        if (workspaces.isEmpty()) {
            this.mySelectRevisionForm.disable();
        } else {
            this.myWorkspacesList.setSelectedIndex(0);
            IdeFocusManager.getGlobalInstance().doWhenFocusSettlesDown(() -> {
                IdeFocusManager.getGlobalInstance().requestFocus(this.myWorkspacesList, true);
            });
        }

    }

    private void applyCurrentValue() throws ConfigurationException {
        if (this.mySelectedWorkspace != null) {
            VersionSpecBase version = this.mySelectRevisionForm.getVersionSpec();
            if (version == null) {
                throw new ConfigurationException("Invalid version specified");
            }

            ((UpdateSettingsForm.WorkspaceSettings)this.myWorkspaceSettings.get(this.mySelectedWorkspace)).version = version;
        }

    }

    public void reset(TFSProjectConfiguration configuration) {
        this.myRecursiveBox.setSelected(configuration.getState().UPDATE_RECURSIVELY);

        Entry e;
        for(Iterator var2 = this.myWorkspaceSettings.entrySet().iterator(); var2.hasNext(); ((UpdateSettingsForm.WorkspaceSettings)e.getValue()).version = configuration.getUpdateWorkspaceInfo((WorkspaceInfo)e.getKey()).getVersion()) {
            e = (Entry)var2.next();
        }

    }

    public void apply(TFSProjectConfiguration configuration) throws ConfigurationException {
        this.applyCurrentValue();
        configuration.getState().UPDATE_RECURSIVELY = this.myRecursiveBox.isSelected();
        Iterator var2 = this.myWorkspaceSettings.entrySet().iterator();

        while(var2.hasNext()) {
            Entry<WorkspaceInfo, UpdateSettingsForm.WorkspaceSettings> e = (Entry)var2.next();
            configuration.getUpdateWorkspaceInfo((WorkspaceInfo)e.getKey()).setVersion(((UpdateSettingsForm.WorkspaceSettings)e.getValue()).version);
        }

    }

    public JComponent getPanel() {
        return this.myPanel;
    }

    public static class WorkspaceSettings {
        public final String serverPath;
        public final boolean isDirectory;
        public VersionSpecBase version;

        public WorkspaceSettings(@NotNull String serverPath, boolean isDirectory) {

            super();
            this.version = LatestVersionSpec.INSTANCE;
            this.serverPath = serverPath;
            this.isDirectory = isDirectory;
        }
    }
}
