//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.util.containers.ContainerUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.Failure;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.jetbrains.annotations.Nullable;

public class ResultWithFailures<T> {
    private final Collection<T> result = new ArrayList();
    private final Collection<Failure> failures = new ArrayList();

    public ResultWithFailures(@Nullable T[] result, @Nullable Failure[] failures) {
        if (result != null) {
            ContainerUtil.addAll(this.result, result);
        }

        if (failures != null) {
            ContainerUtil.addAll(this.failures, failures);
        }

    }

    public ResultWithFailures() {
    }

    public Collection<T> getResult() {
        return this.result;
    }

    public Collection<Failure> getFailures() {
        return this.failures;
    }

    public static <T> ResultWithFailures<T> merge(Collection<? extends ResultWithFailures<T>> results) {
        ResultWithFailures<T> merged = new ResultWithFailures();
        Iterator var2 = results.iterator();

        while(var2.hasNext()) {
            ResultWithFailures<T> r = (ResultWithFailures)var2.next();
            merged.getResult().addAll(r.getResult());
            merged.getFailures().addAll(r.getFailures());
        }

        return merged;
    }
}
