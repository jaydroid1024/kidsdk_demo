package com.qihoo.kids.sdk.util;

import android.util.Log;

import com.qihoo.kids.sdk.BuildConfig;

public class LogUtil {

    private static boolean DEBUG = BuildConfig.DEBUG;
    public static void d(String tag, String s) {
        if(DEBUG) {
            Log.d(tag, s);
        }
    }
}
