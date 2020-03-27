//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.util.text.DateFormatUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Changeset;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.jetbrains.tfsIntegration.core.tfs.TfsUtil;

public class ChangesetsTableModel extends AbstractTableModel {
    private List<Changeset> myChangesets;

    public ChangesetsTableModel() {
    }

    public void setChangesets(List<Changeset> changesets) {
        this.myChangesets = changesets;
        this.fireTableDataChanged();
    }

    public List<Changeset> getChangesets() {
        return this.myChangesets;
    }

    public String getColumnName(int column) {
        return ChangesetsTableModel.Column.values()[column].getCaption();
    }

    public int getRowCount() {
        return this.myChangesets != null ? this.myChangesets.size() : 0;
    }

    public int getColumnCount() {
        return ChangesetsTableModel.Column.values().length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return ChangesetsTableModel.Column.values()[columnIndex].getValue((Changeset)this.myChangesets.get(rowIndex));
    }

    static enum Column {
        Changeset("Changeset", 60) {
            public String getValue(Changeset changeset) {
                return String.valueOf(changeset.getCset());
            }
        },
        Date("Date", 95) {
            public String getValue(Changeset changeset) {
                return DateFormatUtil.formatPrettyDateTime(changeset.getDate().getTimeInMillis());
            }
        },
        User("User", 90) {
            public String getValue(Changeset changeset) {
                return TfsUtil.getNameWithoutDomain(changeset.getOwner());
            }
        },
        Comment("Comment", 180) {
            public String getValue(Changeset changeset) {
                return changeset.getComment();
            }
        };

        private final String myCaption;
        private final int myWidth;

        private Column(String caption, int width) {
            this.myCaption = caption;
            this.myWidth = width;
        }

        public String getCaption() {
            return this.myCaption;
        }

        public abstract String getValue(Changeset var1);

        public int getWidth() {
            return this.myWidth;
        }
    }
}
