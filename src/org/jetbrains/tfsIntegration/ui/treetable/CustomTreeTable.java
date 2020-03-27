//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.ui.treetable;

import com.intellij.ui.treeStructure.treetable.ListTreeTableModelOnColumns;
import com.intellij.ui.treeStructure.treetable.TreeColumnInfo;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableCellRenderer;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.intellij.util.ui.ColumnInfo;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomTreeTable<T> extends TreeTable {
    private List<? extends TreeTableColumn<T>> myColumns;
    private ContentProvider<T> myContentProvider;
    private final CellRenderer<T> myRenderer;
    private final boolean myShowCellFocus;
    private final boolean myShowSelection;
    private static final Object HIDDEN_ROOT = new Object();

    public CustomTreeTable(CellRenderer<T> renderer, boolean showCellFocus, boolean showSelection) {
        this(Collections.singletonList(new CustomTreeTable.FakeColumn()), new CustomTreeTable.FakeContentProvider(), renderer, showCellFocus, showSelection);
    }

    public CustomTreeTable(List columns, ContentProvider<T> contentProvider, CellRenderer<T> renderer, boolean showCellFocus, boolean showSelection) {
        super(createModel(columns, contentProvider));
        this.myColumns = columns;
        this.myContentProvider = contentProvider;
        this.myRenderer = renderer;
        this.myShowCellFocus = showCellFocus;
        this.myShowSelection = showSelection;
        this.initialize();
    }

    public TreeTableCellRenderer createTableRenderer(TreeTableModel treeTableModel) {
        return new TreeTableCellRenderer(this, this.getTree()) {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, value, CustomTreeTable.this.myShowSelection && isSelected, CustomTreeTable.this.myShowCellFocus && hasFocus, row, column);
            }
        };
    }

    private static <T> TreeTableModel createModel(Collection<? extends TreeTableColumn<T>> columns, ContentProvider<T> contentProvider) {
        Collection<ColumnInfo> columnsInfos = new ArrayList(columns.size());
        boolean first = true;

        for(Iterator var4 = columns.iterator(); var4.hasNext(); first = false) {
            TreeTableColumn<T> column = (TreeTableColumn)var4.next();
            if (first) {
                columnsInfos.add(new TreeColumnInfo(column.getCaption()));
            } else {
                columnsInfos.add(new ColumnInfo(column.getCaption()) {
                    public Object valueOf(Object o) {
                        return o;
                    }

                    public Class getColumnClass() {
                        return CustomTreeTable.TableColumnMarker.class;
                    }
                });
            }
        }

        Collection<? extends T> rootObjects = contentProvider.getRoots();
        DefaultMutableTreeNode root;
        if (!rootObjects.isEmpty()) {
            if (rootObjects.size() == 1) {
                root = new DefaultMutableTreeNode(rootObjects.iterator().next());
                addChildren(root, contentProvider);
            } else {
                root = new DefaultMutableTreeNode(HIDDEN_ROOT);
                Iterator var6 = rootObjects.iterator();

                while(var6.hasNext()) {
                    T rootObject = (T) var6.next();
                    DefaultMutableTreeNode subRoot = new DefaultMutableTreeNode(rootObject);
                    addChildren(subRoot, contentProvider);
                    root.add(subRoot);
                }
            }
        } else {
            root = null;
        }

        return new ListTreeTableModelOnColumns(root, (ColumnInfo[])columnsInfos.toArray(ColumnInfo.EMPTY_ARRAY));
    }

    private static <T> void addChildren(DefaultMutableTreeNode parentNode, ContentProvider<T> contentProvider) {
        Collection<? extends T> children = contentProvider.getChildren((T) parentNode.getUserObject());
        Iterator var3 = children.iterator();

        while(var3.hasNext()) {
            T child = (T) var3.next();
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            parentNode.add(childNode);
            addChildren(childNode, contentProvider);
        }

    }

    public void expandAll() {
        for(int i = 0; i < this.getTree().getRowCount(); ++i) {
            this.getTree().expandRow(i);
        }

    }

    public void initialize(List<TreeTableColumn<T>> columns, ContentProvider<T> contentProvider) {
        this.myColumns = columns;
        this.myContentProvider = contentProvider;
        this.setModel(createModel(columns, contentProvider));
        this.initialize();
    }

    public void updateContent() {
        this.setModel(createModel(this.myColumns, this.myContentProvider));
        this.initialize();
    }

    private void initialize() {
        this.setTreeCellRenderer(new CustomTreeTable.TreeColumnRenderer());
        this.setDefaultRenderer(CustomTreeTable.TableColumnMarker.class, new CustomTreeTable.TableColumnRenderer());

        for(int i = 0; i < this.getColumnModel().getColumnCount(); ++i) {
            this.getColumnModel().getColumn(i).setPreferredWidth(((TreeTableColumn)this.myColumns.get(i)).getWidth());
        }

        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)this.getTableModel().getRoot();
        this.setRootVisible(rootNode == null || rootNode.getUserObject() != HIDDEN_ROOT);
    }

    public Collection<T> getSelectedItems() {
        int[] selectedRows = this.getSelectedRows();
        Collection<T> result = new ArrayList(selectedRows.length);
        int[] var3 = selectedRows;
        int var4 = selectedRows.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            int row = var3[var5];
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)this.getTree().getPathForRow(row).getLastPathComponent();
            T userObject = (T) treeNode.getUserObject();
            if (userObject != HIDDEN_ROOT) {
                result.add(userObject);
            }
        }

        return result;
    }

    public void select(T userObject) {
        if (userObject != null) {
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)this.getTableModel().getRoot();
            if (rootNode == null) {
                return;
            }

            DefaultMutableTreeNode node = this.find(rootNode, userObject);
            if (node != null) {
                int row = this.getTree().getRowForPath(new TreePath(node.getPath()));
                this.getSelectionModel().setSelectionInterval(row, row);
            }
        } else {
            this.getSelectionModel().clearSelection();
        }

    }

    @Nullable
    private DefaultMutableTreeNode find(DefaultMutableTreeNode root, @NotNull T userObject) {

        if (userObject.equals(root.getUserObject())) {
            return root;
        } else {
            for(int i = 0; i < root.getChildCount(); ++i) {
                DefaultMutableTreeNode result = this.find((DefaultMutableTreeNode)root.getChildAt(i), userObject);
                if (result != null) {
                    return result;
                }
            }

            return null;
        }
    }

    private static final class TableColumnMarker {
        private TableColumnMarker() {
        }
    }

    private class TableColumnRenderer extends DefaultTableCellRenderer {
        private TableColumnRenderer() {
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, CustomTreeTable.this.myShowSelection && isSelected, CustomTreeTable.this.myShowCellFocus && hasFocus, row, column);
            JLabel label = (JLabel)c;
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
            if (treeNode != null && CustomTreeTable.HIDDEN_ROOT != treeNode.getUserObject()) {
                T typedValue = (T) treeNode.getUserObject();
                CustomTreeTable.this.myRenderer.render(CustomTreeTable.this, (TreeTableColumn)CustomTreeTable.this.myColumns.get(column), typedValue, label);
                return c;
            } else {
                return c;
            }
        }
    }

    private class TreeColumnRenderer extends DefaultTreeCellRenderer {
        private TreeColumnRenderer() {
        }

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            Component c = super.getTreeCellRendererComponent(tree, value, CustomTreeTable.this.myShowSelection && sel, expanded, leaf, row, CustomTreeTable.this.myShowCellFocus && hasFocus);
            JLabel label = (JLabel)c;
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
            if (CustomTreeTable.HIDDEN_ROOT == treeNode.getUserObject()) {
                return c;
            } else {
                T typedValue = (T) treeNode.getUserObject();
                CustomTreeTable.this.myRenderer.render(CustomTreeTable.this, (TreeTableColumn)CustomTreeTable.this.myColumns.get(0), typedValue, label);
                return c;
            }
        }
    }

    private static final class FakeContentProvider<T> implements ContentProvider<T> {
        private FakeContentProvider() {
        }

        public Collection<? extends T> getRoots() {
            return Collections.emptyList();
        }

        public Collection<? extends T> getChildren(@NotNull T parent) {

            return Collections.emptyList();
        }
    }

    private static final class FakeColumn<T> extends TreeTableColumn<T> {
        FakeColumn() {
            super((String)null, 0);
        }

        public String getPresentableString(T value) {
            return "";
        }
    }
}
