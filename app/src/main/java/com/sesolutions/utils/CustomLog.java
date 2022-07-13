package com.sesolutions.utils;

import android.util.Log;

public class CustomLog extends CustomLogRelease {


    public static void e(String tag, String message) {
        Log.e(tag, "" + message);
    }

    public static void e(String tag, String message, Exception e) {
        Log.e(tag, "" + message, e);
    }

    public static void e(Exception e) {
        Log.e("SESOLUTIONS", Log.getStackTraceString(e));
    }

    public static void d(String tag, String message) {
        Log.d(tag, "" + message);
    }


}
