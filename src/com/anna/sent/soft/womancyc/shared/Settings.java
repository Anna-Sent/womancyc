package com.anna.sent.soft.womancyc.shared;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.anna.sent.soft.womancyc.R;

public class Settings {
	private static final String SETTINGS_FILE = "womancycsettings";

	private static SharedPreferences getSettings(Context context) {
		return context.getApplicationContext().getSharedPreferences(
				SETTINGS_FILE, Context.MODE_PRIVATE);
	}

	private static final String DEFAULT_MENSTRUAL_CYCLE_LEN = "pref_default_menstrual_cycle_len";

	public static int getDefaultMenstrualCycleLen(Context context) {
		SharedPreferences settings = getSettings(context);
		return settings.getInt(DEFAULT_MENSTRUAL_CYCLE_LEN, 28);
	}

	public static void setDefaultMenstrualCycleLen(Context context, int value) {
		SharedPreferences settings = getSettings(context);
		Editor editor = settings.edit();
		editor.putInt(DEFAULT_MENSTRUAL_CYCLE_LEN, value);
		editor.commit();
	}

	private static final String USE_AVG = "pref_use_average";

	public static boolean useAverage(Context context) {
		SharedPreferences settings = getSettings(context);
		return settings.getBoolean(USE_AVG, true);
	}

	public static void useAverage(Context context, boolean value) {
		SharedPreferences settings = getSettings(context);
		Editor editor = settings.edit();
		editor.putBoolean(USE_AVG, value);
		editor.commit();
	}

	private static final String LAST_BACKUP_FILE_NAME = "pref_last_backup_file_name";

	public static String getLastBackupFileName(Context context) {
		SharedPreferences settings = getSettings(context);
		return settings.getString(LAST_BACKUP_FILE_NAME,
				context.getString(R.string.defaultFileName));
	}

	public static void setLastBackupFileName(Context context, String value) {
		SharedPreferences settings = getSettings(context);
		Editor editor = settings.edit();
		editor.putString(LAST_BACKUP_FILE_NAME, value);
		editor.commit();
	}
}
