package com.anna.sent.soft.womancyc.utils;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class MyLog {
    private static final String DEFAULT_TAG = "WC";

    private static MyLog sInstance;
    private boolean mIsInitialized;

    public static MyLog getInstance() {
        if (sInstance == null) {
            sInstance = new MyLog();
        }

        return sInstance;
    }

    private static void noFirebaseLogcat(int level, String tag, String msg) {
        switch (level) {
            case Log.ASSERT:
                Log.wtf(tag, msg);
                break;
            case Log.ERROR:
                Log.e(tag, msg);
                break;
            case Log.WARN:
                Log.w(tag, msg);
                break;
            case Log.INFO:
                Log.i(tag, msg);
                break;
            case Log.DEBUG:
                Log.d(tag, msg);
                break;
            case Log.VERBOSE:
                Log.v(tag, msg);
                break;
            default:
                Log.println(level, tag, msg);
                break;
        }
    }

    public void init() {
        if (mIsInitialized) {
            return;
        }

        mIsInitialized = true;
    }

    public void logcat(int level, String msg) {
        if (mIsInitialized) {
            FirebaseCrash.logcat(level, DEFAULT_TAG, msg);
        } else {
            noFirebaseLogcat(level, DEFAULT_TAG, msg);
        }
    }

    public void report(Throwable throwable) {
        if (mIsInitialized) {
            FirebaseCrash.logcat(Log.WARN, DEFAULT_TAG, Log.getStackTraceString(throwable));
            FirebaseCrash.report(throwable);
        } else {
            noFirebaseLogcat(Log.WARN, DEFAULT_TAG, Log.getStackTraceString(throwable));
        }
    }
}
