//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui;

import com.intellij.util.EventDispatcher;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ExtendedItem;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.LockLevel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.tfsIntegration.core.tfs.locks.LockItemModel;

public class LockItemsTableModel extends AbstractTableModel {
    @NotNull
    private final List<? extends LockItemModel> myContent;
    private final EventDispatcher<LockItemsTableModel.Listener> myEventDispatcher;

    public LockItemsTableModel(@NotNull List<? extends LockItemModel> content) {
        super();
        this.myEventDispatcher = EventDispatcher.create(LockItemsTableModel.Listener.class);
        this.myContent = content;
        Collections.sort(this.myContent, LockItemModel.LOCK_ITEM_PARENT_FIRST);
    }

    public int getRowCount() {
        return this.myContent.size();
    }

    public int getColumnCount() {
        return LockItemsTableModel.Column.values().length;
    }

    public String getColumnName(int column) {
        return LockItemsTableModel.Column.values()[column].getName();
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return LockItemsTableModel.Column.values()[columnIndex] == LockItemsTableModel.Column.Selection && ((LockItemModel)this.myContent.get(rowIndex)).getSelectionStatus() != null;
    }

    @Nullable
    public Object getValueAt(int rowIndex, int columnIndex) {
        return LockItemsTableModel.Column.values()[columnIndex].getValue((LockItemModel)this.myContent.get(rowIndex));
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (LockItemsTableModel.Column.values()[columnIndex] == LockItemsTableModel.Column.Selection) {
            ((LockItemModel)this.myContent.get(rowIndex)).setSelectionStatus((Boolean)aValue);
            ((LockItemsTableModel.Listener)this.myEventDispatcher.getMulticaster()).selectionChanged();
        }

    }

    public List<LockItemModel> getSelectedItems() {
        List<LockItemModel> result = new ArrayList();
        Iterator var2 = this.myContent.iterator();

        while(var2.hasNext()) {
            LockItemModel item = (LockItemModel)var2.next();
            if (item.getSelectionStatus() == Boolean.TRUE) {
                result.add(item);
            }
        }

        return result;
    }

    public void addListener(LockItemsTableModel.Listener listener) {
        this.myEventDispatcher.addListener(listener);
    }

    public void removeListener(LockItemsTableModel.Listener listener) {
        this.myEventDispatcher.removeListener(listener);
    }

    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == LockItemsTableModel.Column.Selection.ordinal()) {
            return Boolean.class;
        } else {
            return columnIndex == LockItemsTableModel.Column.Item.ordinal() ? ExtendedItem.class : super.getColumnClass(columnIndex);
        }
    }

    static enum Column {
        Selection("", 5) {
            public Boolean getValue(LockItemModel item) {
                return item.getSelectionStatus();
            }
        },
        Item("Item", 550) {
            public ExtendedItem getValue(LockItemModel item) {
                return item.getExtendedItem();
            }
        },
        Lock("Current Lock", 110) {
            public String getValue(LockItemModel item) {
                LockLevel lock = item.getExtendedItem().getLock();
                return lock == null ? "" : lock.getValue();
            }
        },
        LockOwner("Locked By", 130) {
            public String getValue(LockItemModel item) {
                String lockOwner = item.getLockOwner();
                return lockOwner == null ? "" : lockOwner;
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

        @Nullable
        public abstract Object getValue(LockItemModel var1);
    }

    public interface Listener extends EventListener {
        void selectionChanged();
    }
}
