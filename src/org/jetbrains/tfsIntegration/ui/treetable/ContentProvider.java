package org.jetbrains.tfsIntegration.ui.treetable;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public interface ContentProvider<T> {
  Collection<? extends T> getRoots();
  
  Collection<? extends T> getChildren(@NotNull T paramT);
}


/* Location:              C:\Users\Li Jie\Downloads\tfsIntegration\lib\tfsIntegration.jar!\org\jetbrains\tfsIntegratio\\ui\treetable\ContentProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.1
 */