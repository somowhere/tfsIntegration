//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.VersionControlLabel;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class LabelsTableModel extends AbstractTableModel {
    private List<VersionControlLabel> myLabels;

    public LabelsTableModel() {
    }

    public void setLabels(List<VersionControlLabel> labels) {
        this.myLabels = labels;
        this.fireTableDataChanged();
    }

    public List<VersionControlLabel> getLabels() {
        return this.myLabels;
    }

    public String getColumnName(int column) {
        return LabelsTableModel.Column.values()[column].getCaption();
    }

    public int getRowCount() {
        return this.myLabels != null ? this.myLabels.size() : 0;
    }

    public int getColumnCount() {
        return LabelsTableModel.Column.values().length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return LabelsTableModel.Column.values()[columnIndex].getValue((VersionControlLabel)this.myLabels.get(rowIndex));
    }

    private static enum Column {
        Name("Name") {
            public String getValue(VersionControlLabel label) {
                return label.getName();
            }
        },
        Scope("Scope") {
            public String getValue(VersionControlLabel label) {
                return label.getScope();
            }
        },
        Owner("Owner") {
            public String getValue(VersionControlLabel label) {
                return label.getOwner();
            }
        };

        private final String myCaption;

        private Column(String caption) {
            this.myCaption = caption;
        }

        public String getCaption() {
            return this.myCaption;
        }

        public abstract String getValue(VersionControlLabel var1);
    }
}
