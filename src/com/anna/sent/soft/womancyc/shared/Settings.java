package com.anna.sent.soft.womancyc.shared;

import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.anna.sent.soft.womancyc.R;

public class Settings {
	private static class SharedPreferencesWrapper implements SharedPreferences {
		private SharedPreferences mSettings;

		public SharedPreferencesWrapper(SharedPreferences settings) {
			mSettings = settings;
		}

		@Override
		public Map<String, ?> getAll() {
			return mSettings.getAll();
		}

		@Override
		public String getString(String key, String defValue) {
			try {
				return mSettings.getString(key, defValue);
			} catch (ClassCastException e) {
				return defValue;
			}
		}

		@SuppressLint("NewApi")
		@Override
		public Set<String> getStringSet(String key, Set<String> defValues) {
			try {
				return mSettings.getStringSet(key, defValues);
			} catch (ClassCastException e) {
				return defValues;
			}
		}

		@Override
		public int getInt(String key, int defValue) {
			try {
				return mSettings.getInt(key, defValue);
			} catch (ClassCastException e) {
				return defValue;
			}
		}

		@Override
		public long getLong(String key, long defValue) {
			try {
				return mSettings.getLong(key, defValue);
			} catch (ClassCastException e) {
				return defValue;
			}
		}

		@Override
		public float getFloat(String key, float defValue) {
			try {
				return mSettings.getFloat(key, defValue);
			} catch (ClassCastException e) {
				return defValue;
			}
		}

		@Override
		public boolean getBoolean(String key, boolean defValue) {
			try {
				return mSettings.getBoolean(key, defValue);
			} catch (ClassCastException e) {
				return defValue;
			}
		}

		@Override
		public boolean contains(String key) {
			return mSettings.contains(key);
		}

		@Override
		public Editor edit() {
			return mSettings.edit();
		}

		@Override
		public void registerOnSharedPreferenceChangeListener(
				OnSharedPreferenceChangeListener listener) {
			mSettings.registerOnSharedPreferenceChangeListener(listener);
		}

		@Override
		public void unregisterOnSharedPreferenceChangeListener(
				OnSharedPreferenceChangeListener listener) {
			mSettings.registerOnSharedPreferenceChangeListener(listener);
		}
	}

	private static SharedPreferences getDefaultSettings(Context context) {
		return new SharedPreferencesWrapper(
				PreferenceManager.getDefaultSharedPreferences(context));
	}

	public static final String KEY_PREF_LOCK_AUTOMATICALLY = "pref_lock_automatically";

	public static boolean lockAutomatically(Context context) {
		SharedPreferences settings = getDefaultSettings(context);
		return settings.getBoolean(KEY_PREF_LOCK_AUTOMATICALLY, context
				.getResources().getBoolean(R.bool.lockAutomatically));
	}

	public static void lockAutomatically(Context context, boolean value) {
		SharedPreferences settings = getDefaultSettings(context);
		Editor editor = settings.edit();
		editor.putBoolean(KEY_PREF_LOCK_AUTOMATICALLY, value);
		editor.commit();
	}

	public static final String KEY_PREF_HIDE_WIDGET = "pref_hide_widget";

	public static boolean hideWidget(Context context) {
		SharedPreferences settings = getDefaultSettings(context);
		return settings.getBoolean(KEY_PREF_HIDE_WIDGET, context.getResources()
				.getBoolean(R.bool.hideWidget));
	}

	public static void hideWidget(Context context, boolean value) {
		SharedPreferences settings = getDefaultSettings(context);
		Editor editor = settings.edit();
		editor.putBoolean(KEY_PREF_HIDE_WIDGET, value);
		editor.commit();
	}

	private static final String KEY_PREF_IS_BLOCKED = "pref_is_blocked";

	public static boolean isBlocked(Context context) {
		SharedPreferences settings = getDefaultSettings(context);
		return settings.getBoolean(KEY_PREF_IS_BLOCKED, false);
	}

	public static void isBlocked(Context context, boolean value) {
		SharedPreferences settings = getDefaultSettings(context);
		Editor editor = settings.edit();
		editor.putBoolean(KEY_PREF_IS_BLOCKED, value);
		editor.commit();
	}

	public static final String KEY_PREF_PASSWORD = "pref_password";

	public static String getPassword(Context context) {
		SharedPreferences settings = getDefaultSettings(context);
		return settings.getString(KEY_PREF_PASSWORD, "");
	}

	public static void setPassword(Context context, String value) {
		SharedPreferences settings = getDefaultSettings(context);
		Editor editor = settings.edit();
		editor.putString(KEY_PREF_PASSWORD, value);
		editor.commit();
	}

	public static boolean isPasswordSet(Context context) {
		String password = getPassword(context);
		return !password.equals("");
	}

	public static boolean isApplicationLocked(Context context) {
		return isPasswordSet(context)
				&& (isBlocked(context) || lockAutomatically(context));
	}

	public static final String KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN = "pref_default_menstrual_cycle_len";

	public static int getDefaultMenstrualCycleLen(Context context) {
		SharedPreferences settings = getDefaultSettings(context);
		return settings.getInt(KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN, context
				.getResources().getInteger(R.integer.defaultMenstrualCycleLen));
	}

	public static void setDefaultMenstrualCycleLen(Context context, int value) {
		SharedPreferences settings = getDefaultSettings(context);
		Editor editor = settings.edit();
		editor.putInt(KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN, value);
		editor.commit();
	}

	public static final String KEY_PREF_USE_AVG = "pref_use_average";

	public static boolean useAverage(Context context) {
		SharedPreferences settings = getDefaultSettings(context);
		return settings.getBoolean(KEY_PREF_USE_AVG, context.getResources()
				.getBoolean(R.bool.useAverage));
	}

	public static void useAverage(Context context, boolean value) {
		SharedPreferences settings = getDefaultSettings(context);
		Editor editor = settings.edit();
		editor.putBoolean(KEY_PREF_USE_AVG, value);
		editor.commit();
	}

	public static final String KEY_PREF_THEME = "pref_theme";

	public static int getTheme(Context context) {
		SharedPreferences settings = getDefaultSettings(context);
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
		SharedPreferences settings = getDefaultSettings(context);
		Editor editor = settings.edit();
		editor.putString(KEY_PREF_THEME, String.valueOf(value));
		editor.commit();
	}
}