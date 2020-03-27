//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Conflict;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ConflictType;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.jetbrains.tfsIntegration.core.tfs.VersionControlPath;

public class ConflictsTableModel extends AbstractTableModel {
    private List<Conflict> myConflicts;

    public ConflictsTableModel() {
    }

    public List<Conflict> getMergeData() {
        return this.myConflicts;
    }

    public String getColumnName(int column) {
        return ConflictsTableModel.Column.values()[column].getCaption();
    }

    public int getRowCount() {
        return this.myConflicts != null ? this.myConflicts.size() : 0;
    }

    public int getColumnCount() {
        return ConflictsTableModel.Column.values().length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        Conflict conflict = (Conflict)this.myConflicts.get(rowIndex);
        return ConflictsTableModel.Column.values()[columnIndex].getValue(conflict);
    }

    public List<Conflict> getConflicts() {
        return this.myConflicts;
    }

    public void setConflicts(List<Conflict> conflicts) {
        this.myConflicts = conflicts;
        this.fireTableDataChanged();
    }

    public static enum Column {
        Name("Name") {
            public String getValue(Conflict conflict) {
                return conflict.getCtype() == ConflictType.Merge ? VersionControlPath.localPathFromTfsRepresentation(conflict.getTgtlitem() != null ? conflict.getTgtlitem() : conflict.getSrclitem()) : VersionControlPath.localPathFromTfsRepresentation(conflict.getSrclitem() != null ? conflict.getSrclitem() : conflict.getTgtlitem());
            }
        };

        private final String myCaption;

        private Column(String caption) {
            this.myCaption = caption;
        }

        public String getCaption() {
            return this.myCaption;
        }

        public abstract String getValue(Conflict var1);
    }
}
