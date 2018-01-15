package com.anna.sent.soft.womancyc.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.anna.sent.soft.settings.SharedPreferencesWrapper;
import com.anna.sent.soft.womancyc.R;

public class Settings {
    public static final String KEY_PREF_LOCK_AUTOMATICALLY = "pref_lock_automatically";
    public static final String KEY_PREF_HIDE_WIDGET = "pref_hide_widget";
    public static final String KEY_PREF_PASSWORD = "pref_password";
    public static final String KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN = "pref_default_menstrual_cycle_len";
    public static final String KEY_PREF_USE_AVG = "pref_use_average";
    private static final String KEY_PREF_IS_BLOCKED = "pref_is_blocked";

    public static SharedPreferences getSettings(Context context) {
        return new SharedPreferencesWrapper(
                PreferenceManager.getDefaultSharedPreferences(context));
    }

    public static boolean lockAutomatically(Context context) {
        SharedPreferences settings = getSettings(context);
        return settings.getBoolean(KEY_PREF_LOCK_AUTOMATICALLY, context
                .getResources().getBoolean(R.bool.lockAutomatically));
    }

    public static void lockAutomatically(Context context, boolean value) {
        SharedPreferences settings = getSettings(context);
        Editor editor = settings.edit();
        editor.putBoolean(KEY_PREF_LOCK_AUTOMATICALLY, value);
        editor.apply();
    }

    public static boolean hideWidget(Context context) {
        SharedPreferences settings = getSettings(context);
        return settings.getBoolean(KEY_PREF_HIDE_WIDGET, context.getResources()
                .getBoolean(R.bool.hideWidget));
    }

    public static void hideWidget(Context context, boolean value) {
        SharedPreferences settings = getSettings(context);
        Editor editor = settings.edit();
        editor.putBoolean(KEY_PREF_HIDE_WIDGET, value);
        editor.apply();
    }

    public static boolean isBlocked(Context context) {
        SharedPreferences settings = getSettings(context);
        return settings.getBoolean(KEY_PREF_IS_BLOCKED, false);
    }

    public static void isBlocked(Context context, boolean value) {
        SharedPreferences settings = getSettings(context);
        Editor editor = settings.edit();
        editor.putBoolean(KEY_PREF_IS_BLOCKED, value);
        editor.apply();
    }

    public static String getPassword(Context context) {
        SharedPreferences settings = getSettings(context);
        return settings.getString(KEY_PREF_PASSWORD, "");
    }

    public static void setPassword(Context context, String value) {
        SharedPreferences settings = getSettings(context);
        Editor editor = settings.edit();
        editor.putString(KEY_PREF_PASSWORD, value);
        editor.apply();
    }

    public static void clearPassword(Context context) {
        SharedPreferences settings = getSettings(context);
        Editor editor = settings.edit();
        editor.remove(KEY_PREF_PASSWORD);
        editor.apply();
    }

    public static boolean isPasswordSet(Context context) {
        String password = getPassword(context);
        return !TextUtils.isEmpty(password);
    }

    public static boolean isApplicationLocked(Context context) {
        return isPasswordSet(context)
                && (isBlocked(context) || lockAutomatically(context));
    }

    public static int getDefaultMenstrualCycleLen(Context context) {
        SharedPreferences settings = getSettings(context);
        return settings.getInt(KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN, context
                .getResources().getInteger(R.integer.defaultMenstrualCycleLen));
    }

    public static void setDefaultMenstrualCycleLen(Context context, int value) {
        SharedPreferences settings = getSettings(context);
        Editor editor = settings.edit();
        editor.putInt(KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN, value);
        editor.apply();
    }

    public static boolean useAverage(Context context) {
        SharedPreferences settings = getSettings(context);
        return settings.getBoolean(KEY_PREF_USE_AVG, context.getResources()
                .getBoolean(R.bool.useAverage));
    }

    public static void useAverage(Context context, boolean value) {
        SharedPreferences settings = getSettings(context);
        Editor editor = settings.edit();
        editor.putBoolean(KEY_PREF_USE_AVG, value);
        editor.apply();
    }
}
