package com.anna.sent.soft.womancyc.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.anna.sent.soft.womancyc.R;

public class Settings {
	private static SharedPreferences getSettings(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}

	public static final String KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN = "pref_default_menstrual_cycle_len";

	public static int getDefaultMenstrualCycleLen(Context context) {
		SharedPreferences settings = getSettings(context);
		return settings.getInt(KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN, context
				.getResources().getInteger(R.integer.defaultMenstrualCycleLen));
	}

	public static void setDefaultMenstrualCycleLen(Context context, int value) {
		SharedPreferences settings = getSettings(context);
		Editor editor = settings.edit();
		editor.putInt(KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN, value);
		editor.commit();
	}

	public static final String KEY_PREF_USE_AVG = "pref_use_average";

	public static boolean useAverage(Context context) {
		SharedPreferences settings = getSettings(context);
		return settings.getBoolean(KEY_PREF_USE_AVG, context.getResources()
				.getBoolean(R.bool.useAverage));
	}

	public static void useAverage(Context context, boolean value) {
		SharedPreferences settings = getSettings(context);
		Editor editor = settings.edit();
		editor.putBoolean(KEY_PREF_USE_AVG, value);
		editor.commit();
	}

	public static final String KEY_PREF_THEME = "pref_theme";

	public static int getTheme(Context context) {
		SharedPreferences settings = getSettings(context);
		int defaultValue = context.getResources().getInteger(
				R.integer.defaultTheme);
		String value = settings.getString(KEY_PREF_THEME, "");
		int result = defaultValue;
		if (!value.equals("")) {
			try {
				result = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				result = defaultValue;
			}
		}

		return result;
	}

	public static void setTheme(Context context, int value) {
		SharedPreferences settings = getSettings(context);
		Editor editor = settings.edit();
		editor.putString(KEY_PREF_THEME, String.valueOf(value));
		editor.commit();
	}
}
