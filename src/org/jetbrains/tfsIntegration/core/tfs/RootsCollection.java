//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.DistinctRootsCollection;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public abstract class RootsCollection<T> {
    public RootsCollection() {
    }

    public static class VirtualFileRootsCollection extends DistinctRootsCollection<VirtualFile> {
        public VirtualFileRootsCollection() {
        }

        public VirtualFileRootsCollection(Collection<? extends VirtualFile> items) {
            super(items);
        }

        public VirtualFileRootsCollection(VirtualFile[] items) {
            super(items);
        }

        protected boolean isAncestor(@NotNull VirtualFile parent, @NotNull VirtualFile child) {

            return VfsUtilCore.isAncestor(parent, child, false);
        }
    }

    public static class ItemPathRootsCollection extends DistinctRootsCollection<ItemPath> {
        public ItemPathRootsCollection() {
        }

        public ItemPathRootsCollection(Collection<? extends ItemPath> items) {
            super(items);
        }

        protected boolean isAncestor(@NotNull ItemPath parent, @NotNull ItemPath child) {

            return child.getLocalPath().isUnder(parent.getLocalPath(), false);
        }
    }

    public static class FilePathRootsCollection extends DistinctRootsCollection<FilePath> {
        public FilePathRootsCollection() {
        }

        public FilePathRootsCollection(Collection<? extends FilePath> items) {
            super(items);
        }

        protected boolean isAncestor(@NotNull FilePath parent, @NotNull FilePath child) {

            return child.isUnder(parent, false);
        }
    }
}
