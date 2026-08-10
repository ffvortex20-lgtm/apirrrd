package com.vortex.hub.network;

import android.util.Log;

public class VortexLogger {
    private static final String TAG = "VortexNetworkAgent";

    public static void i(String message) {
        Log.i(TAG, "[INFO] " + message);
    }

    public static void w(String message) {
        Log.w(TAG, "[WARN] " + message);
    }

    public static void e(String message, Throwable throwable) {
        Log.e(TAG, "[ERROR] " + message, throwable);
    }
}
