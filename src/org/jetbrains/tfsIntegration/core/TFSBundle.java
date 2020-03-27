//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.jetbrains.tfsIntegration.core;

import com.intellij.CommonBundle;
import com.intellij.reference.SoftReference;
import java.lang.ref.Reference;
import java.util.ResourceBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class TFSBundle {
    private static Reference<ResourceBundle> ourBundle;
    @NonNls
    private static final String BUNDLE = "org.jetbrains.tfsIntegration.core.TFSBundle";

    public static String message(@NotNull @PropertyKey(resourceBundle = "org.jetbrains.tfsIntegration.core.TFSBundle") String key, @NotNull Object... params) {

        return CommonBundle.message(getBundle(), key, params);
    }

    private TFSBundle() {
    }

    private static ResourceBundle getBundle() {
        ResourceBundle bundle = (ResourceBundle)SoftReference.dereference(ourBundle);
        if (bundle == null) {
            bundle = ResourceBundle.getBundle("org.jetbrains.tfsIntegration.core.TFSBundle");
            ourBundle = new java.lang.ref.SoftReference(bundle);
        }

        return bundle;
    }
}
