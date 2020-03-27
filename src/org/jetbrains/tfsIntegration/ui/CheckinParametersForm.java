//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.table.TableView;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.EmptyIcon;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters;
import org.jetbrains.tfsIntegration.checkin.CheckinPoliciesManager;
import org.jetbrains.tfsIntegration.checkin.NotInstalledPolicyFailure;
import org.jetbrains.tfsIntegration.checkin.PolicyFailure;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters.CheckinNote;
import org.jetbrains.tfsIntegration.checkin.CheckinParameters.Severity;
import org.jetbrains.tfsIntegration.core.tfs.ServerInfo;

public class CheckinParametersForm implements Disposable {
    private static final Color REQUIRED_BACKGROUND_COLOR = new Color(252, 248, 199);
    private static final Color NOT_INSTALLED_POLICY_COLOR = UIUtil.getInactiveTextColor();
    private static final Icon WARNING_ICON = UIUtil.getBalloonWarningIcon();
    private static final Icon ERROR_ICON = UIUtil.getBalloonErrorIcon();
    private static final Icon EMPTY_ICON;
    private JPanel myContentPane;
    private JComboBox myServersCombo;
    private JPanel myServerChooserPanel;
    private JTabbedPane myTabbedPane;
    private JPanel myNotesPanel;
    private JPanel myCheckinNotesTab;
    private JLabel myErrorLabel;
    private JPanel myWorkItemsTab;
    private JPanel myPoliciesTab;
    private TableView<PolicyFailure> myWarningsTable;
    private JButton myEvaluateButton;
    private WorkItemsPanel myWorkItemsPanel;
    private final CheckinParameters myState;
    private final Project myProject;
    private static final MultiLineTableRenderer WARNING_TABLE_RENDERER;
    public static final ColumnInfo<PolicyFailure, PolicyFailure> WARNING_COLUMN_INFO;

    public CheckinParametersForm(CheckinParameters state, Project project) {
        this.myProject = project;
        this.myState = state;
        this.myServersCombo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ServerInfo) {
                    label.setText(((ServerInfo)value).getPresentableUri());
                }

                label.setIcon(UiConstants.ICON_SERVER);
                return label;
            }
        });
        this.myServersCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CheckinParametersForm.this.myWorkItemsPanel.update();
                CheckinParametersForm.this.udpateCheckinNotes();
                CheckinParametersForm.this.updatePoliciesWarnings();
                CheckinParametersForm.this.updateErrorMessage(false);
            }
        });
        this.myWarningsTable.setModelAndUpdateColumns(new ListTableModel(new ColumnInfo[]{WARNING_COLUMN_INFO}));
        this.myWarningsTable.setTableHeader((JTableHeader)null);
        (new DoubleClickListener() {
            protected boolean onDoubleClick(MouseEvent e) {
                PolicyFailure failure = (PolicyFailure)CheckinParametersForm.this.myWarningsTable.getSelectedObject();
                if (failure != null) {
                    failure.activate(CheckinParametersForm.this.myProject);
                }

                return true;
            }
        }).installOn(this.myWarningsTable);
        this.myEvaluateButton.setEnabled(this.myState.evaluationEnabled() && this.myState.getPoliciesLoadError() == null);
        this.myEvaluateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CheckinParametersForm.this.evaluatePolicies();
            }
        });
        this.myServerChooserPanel.setVisible(this.myState.getServers().size() > 1);
        this.myServersCombo.setModel(new DefaultComboBoxModel(this.myState.getServers().toArray()));
        Pair<ServerInfo, ? extends Component> pair = this.getInitialSelectedTab();
        this.myServersCombo.setSelectedItem(pair.first);
        this.myTabbedPane.setSelectedIndex(this.myTabbedPane.indexOfComponent((Component)pair.second));
    }

    public CheckinParameters getState() {
        return this.myState;
    }

    public Project getProject() {
        return this.myProject;
    }

    private void evaluatePolicies() {
        boolean completed = ProgressManager.getInstance().runProcessWithProgressSynchronously(() -> {
            ProgressIndicator pi = ProgressManager.getInstance().getProgressIndicator();
            pi.setIndeterminate(true);
            this.myState.evaluatePolicies(pi);
        }, "Evaluating Checkin Policies", true, this.myProject);
        if (completed) {
            this.updatePoliciesWarnings();
            this.updateErrorMessage(false);
        }

    }

    private void updatePoliciesWarnings() {
        List<PolicyFailure> failures = new ArrayList();
        if (!this.myState.evaluationEnabled()) {
            failures.add(new PolicyFailure(CheckinPoliciesManager.DUMMY_POLICY, "Evaluation of checkin policies was disabled", "Use Project Settings | TFS configuration settings to enable checkin policies evaluation"));
        } else if (this.myState.getPoliciesLoadError() != null) {
            failures.add(new PolicyFailure(CheckinPoliciesManager.DUMMY_POLICY, "Cannot load checkin policies definitions", this.myState.getPoliciesLoadError()));
        } else if (!this.myState.policiesEvaluated()) {
            failures.add(new PolicyFailure(CheckinPoliciesManager.DUMMY_POLICY, "Checkin policies were not evaluated") {
                public void activate(@NotNull Project project) {


                    CheckinParametersForm.this.evaluatePolicies();
                }
            });
        } else if (this.myState.getFailures(this.getSelectedServer()).isEmpty()) {
            failures.add(new PolicyFailure(CheckinPoliciesManager.DUMMY_POLICY, "All checkin policies are satisfied") {
                public void activate(@NotNull Project project) {


                }
            });
        } else {
            failures.addAll(this.myState.getFailures(this.getSelectedServer()));
        }

        ((ListTableModel)this.myWarningsTable.getModel()).setItems(failures);
    }

    private Pair<ServerInfo, ? extends Component> getInitialSelectedTab() {
        Iterator var1 = this.myState.getServers().iterator();

        ServerInfo server;
        do {
            if (!var1.hasNext()) {
                var1 = this.myState.getServers().iterator();

                do {
                    if (!var1.hasNext()) {
                        return Pair.create(this.myState.getServers().get(0), this.myWorkItemsTab);
                    }

                    server = (ServerInfo)var1.next();
                } while(!this.myState.hasPolicyFailures(server));

                return Pair.create(server, this.myPoliciesTab);
            }

            server = (ServerInfo)var1.next();
        } while(!this.myState.hasEmptyNotes(server));

        return Pair.create(server, this.myCheckinNotesTab);
    }

    private void createUIComponents() {
        this.myWorkItemsPanel = new WorkItemsPanel(this);
        Disposer.register(this, this.myWorkItemsPanel);
        this.myErrorLabel = new JLabel() {
            public void updateUI() {
                this.setUI(new MultiLineLabelUI());
            }

            public Dimension getMinimumSize() {
                return this.getPreferredSize();
            }
        };
        this.myErrorLabel.setVerticalTextPosition(1);
    }

    public ServerInfo getSelectedServer() {
        return (ServerInfo)this.myServersCombo.getSelectedItem();
    }

    public JComponent getContentPane() {
        return this.myContentPane;
    }

    private void udpateCheckinNotes() {
        this.myNotesPanel.removeAll();
        List<CheckinNote> notes = this.myState.getCheckinNotes(this.getSelectedServer());
        Insets labelInsets = new Insets(0, 0, 5, 0);
        Insets fieldInsets = new Insets(0, 20, 10, 0);
        int i = 0;
        Iterator var5 = notes.iterator();

        while(var5.hasNext()) {
            final CheckinNote note = (CheckinNote)var5.next();
            String text = note.name + ":";
            if (note.required) {
                text = "<html><b>" + text + "</b></html>";
            }

            JLabel label = new JLabel(text);
            GridBagConstraints c = new GridBagConstraints(0, i++, 1, 1, 0.0D, 0.0D, 17, 0, labelInsets, 0, 0);
            this.myNotesPanel.add(label, c);
            final JTextField field = new JTextField(note.value);
            if (note.required) {
                field.setBackground(REQUIRED_BACKGROUND_COLOR);
            }

            c = new GridBagConstraints(0, i++, 1, 1, 0.0D, 0.0D, 18, 2, fieldInsets, 0, 0);
            this.myNotesPanel.add(field, c);
            field.getDocument().addDocumentListener(new DocumentAdapter() {
                protected void textChanged(@NotNull DocumentEvent e) {


                    note.value = field.getText();
                    CheckinParametersForm.this.updateErrorMessage(true);
                }
            });
        }

        this.myNotesPanel.add(new JPanel(), new GridBagConstraints(0, i, 1, 1, 1.0D, 10.0D, 17, 3, labelInsets, 0, 0));
    }

    public void updateErrorMessage(boolean evaluateNotes) {
        if (evaluateNotes) {
            this.myState.validateNotes();
        }

        Icon icon = this.myState.hasPolicyFailures(this.getSelectedServer()) ? WARNING_ICON : EMPTY_ICON;
        this.myTabbedPane.setIconAt(this.myTabbedPane.indexOfComponent(this.myPoliciesTab), icon);
        icon = this.myState.hasEmptyNotes(this.getSelectedServer()) ? ERROR_ICON : EMPTY_ICON;
        this.myTabbedPane.setIconAt(this.myTabbedPane.indexOfComponent(this.myCheckinNotesTab), icon);
        Pair<String, Severity> message = this.myState.getValidationMessage(Severity.BOTH);
        this.myErrorLabel.setText((String)Pair.getFirst(message));
        this.myErrorLabel.setIcon(message == null ? null : (message.second == Severity.ERROR ? ERROR_ICON : WARNING_ICON));
    }

    public void dispose() {
    }

    static {
        EMPTY_ICON = EmptyIcon.create(0, WARNING_ICON.getIconHeight());
        WARNING_TABLE_RENDERER = new MultiLineTableRenderer() {
            protected void customize(JTable table, JTextArea textArea, boolean isSelected, Object value) {
                PolicyFailure failure = (PolicyFailure)value;
                textArea.setText(failure.getMessage());
                String tooltip = failure.getTooltipText();
                textArea.setToolTipText(StringUtil.isNotEmpty(tooltip) ? tooltip : null);
                Color foreground;
                if (isSelected) {
                    foreground = table.getSelectionForeground();
                } else {
                    foreground = failure instanceof NotInstalledPolicyFailure ? CheckinParametersForm.NOT_INSTALLED_POLICY_COLOR : table.getForeground();
                }

                textArea.setForeground(foreground);
            }
        };
        WARNING_COLUMN_INFO = new ColumnInfo<PolicyFailure, PolicyFailure>("message") {
            public PolicyFailure valueOf(PolicyFailure policyFailure) {
                return policyFailure;
            }

            public TableCellRenderer getRenderer(PolicyFailure policyFailure) {
                return CheckinParametersForm.WARNING_TABLE_RENDERER;
            }
        };
    }
}
