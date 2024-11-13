package com.arashivision.sdk.demo.util;

import com.arashivision.sdk.demo.MyApp;

import java.io.File;

public class StorageUtils {
    private static String sRootPathCache;

    public static String getInternalRootPath() {
        if (sRootPathCache != null) {
            return sRootPathCache;
        } else {
            File file = MyApp.getInstance().getExternalFilesDir("");
            if (file != null) {
                sRootPathCache = file.getAbsolutePath();
                return sRootPathCache;
            } else {
                return null;
            }
        }
    }

}
