package com.dj.pluginlib;

import android.util.Log;

public class PluginLog {
    private final static String TAG = "pluginLib";
    private final static boolean IS_DEBUG = true;

    public static void log(String info) {
        if (IS_DEBUG) Log.e(TAG, info);
    }
    public static void log(String tag,String info) {
        if (IS_DEBUG) Log.e(tag, ">>>>>>>>>>>>>>>"+info+"<<<<<<<<<<<<<<<");
    }
}
