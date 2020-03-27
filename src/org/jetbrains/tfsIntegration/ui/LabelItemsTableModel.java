//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Item;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.tfsIntegration.core.tfs.labels.ItemAndVersion;
import org.jetbrains.tfsIntegration.core.tfs.version.VersionSpecBase;

public class LabelItemsTableModel extends AbstractTableModel {
    private List<ItemAndVersion> myContent = Collections.emptyList();

    public LabelItemsTableModel() {
    }

    public void setContent(@NotNull List<ItemAndVersion> newContent) {

        this.myContent = newContent;
        this.fireTableDataChanged();
    }

    public int getRowCount() {
        return this.myContent.size();
    }

    public int getColumnCount() {
        return LabelItemsTableModel.Column.values().length;
    }

    public String getColumnName(int column) {
        return LabelItemsTableModel.Column.values()[column].getName();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return LabelItemsTableModel.Column.values()[columnIndex].getValue(this.getItem(rowIndex));
    }

    public ItemAndVersion getItem(int rowIndex) {
        return (ItemAndVersion)this.myContent.get(rowIndex);
    }

    static enum Column {
        Item("Item", 300) {
            public Item getValue(ItemAndVersion itemAndVersion) {
                return itemAndVersion.getItem();
            }
        },
        Version("Version", 100) {
            public String getValue(ItemAndVersion itemAndVersion) {
                return ((VersionSpecBase)itemAndVersion.getVersionSpec()).getPresentableString();
            }
        };

        private final String myName;
        private final int myWidth;

        private Column(String name, int width) {
            this.myName = name;
            this.myWidth = width;
        }

        public String getName() {
            return this.myName;
        }

        public int getWidth() {
            return this.myWidth;
        }

        public abstract Object getValue(ItemAndVersion var1);
    }
}
