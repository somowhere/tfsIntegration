//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core.tfs;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ArrayUtil;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType;
import com.microsoft.schemas.teamfoundation._2005._06.versioncontrol.clientservices._03.ChangeType_type0;
import org.jetbrains.annotations.Nullable;

public class ChangeTypeMask {
    @Nullable
    private ChangeType_type0[] myValues;

    public ChangeTypeMask(ChangeType changeType) {
        this.myValues = changeType != null ? changeType.getChangeType_type0() : null;
    }

    public boolean containsAll(ChangeType_type0... values) {
        if (this.myValues == null) {
            return false;
        } else {
            ChangeType_type0[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ChangeType_type0 value = var2[var4];
                if (!ArrayUtil.contains(value, this.myValues)) {
                    return false;
                }
            }

            return true;
        }
    }

    public boolean contains(ChangeType_type0 value) {
        return this.containsAny(value);
    }

    public boolean containsAny(ChangeType_type0... values) {
        if (this.myValues == null) {
            return false;
        } else {
            ChangeType_type0[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ChangeType_type0 value = var2[var4];
                if (ArrayUtil.contains(value, this.myValues)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean containsOnly(ChangeType_type0... values) {
        return this.myValues != null && this.myValues.length == values.length && this.containsAll(values);
    }

    public void remove(ChangeType_type0... values) {
        if (this.myValues != null) {
            ChangeType_type0[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                ChangeType_type0 value = var2[var4];
                this.myValues = (ChangeType_type0[])ArrayUtil.remove(this.myValues, value);
            }

        }
    }

    public boolean isEmpty() {
        return this.myValues == null || this.myValues.length == 0;
    }

    public int size() {
        return this.myValues != null ? this.myValues.length : 0;
    }

    public String toString() {
        return this.isEmpty() ? "(empty)" : StringUtil.join(this.myValues, (changeType_type0) -> {
            return changeType_type0.getValue();
        }, ",");
    }
}
