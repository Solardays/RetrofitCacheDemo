package com.cc.cachedemo.utils;

import android.util.Log;

public class LogUtil {
    private static boolean DEBUG = true;

    public static void setDebugEnable(boolean debug) {
        DEBUG = debug;
    }

    private static String defaultTag() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        String className = stackTraceElement.getClassName();
        int line = stackTraceElement.getLineNumber();
        return className + " [" + line + "]";
    }

    public static void d(String log) {
        if (DEBUG)
            Log.d(defaultTag(), log);
    }


    public static void e(String log) {
        if (DEBUG)
            Log.e(defaultTag(), log);
    }


    public static void v(String log) {
        if (DEBUG)
            Log.v(defaultTag(), log);
    }


    public static void i(String log) {
        if (DEBUG)
            Log.i(defaultTag(), log);
    }


    public static void w(String log) {
        if (DEBUG)
            Log.w(defaultTag(), log);
    }

}
