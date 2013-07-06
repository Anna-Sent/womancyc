package com.anna.sent.soft.womancyc;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = "moo";
	private static final boolean DEBUG = true;

	private String wrapMsg(String msg) {
		return getClass().getSimpleName() + ": " + msg;
	}

	@SuppressWarnings("unused")
	private void log(String msg) {
		if (DEBUG) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	@SuppressWarnings("unused")
	private void log(String msg, boolean debug) {
		if (DEBUG && debug) {
			Log.d(TAG, wrapMsg(msg));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		ThemeUtils.onActivityCreateSetTheme(this);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

		Preference prefMcl = findPreference(Settings.KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN);
		prefMcl.setSummary(getString(
				R.string.pref_default_menstrual_cycle_len_summary,
				String.valueOf(Settings.getDefaultMenstrualCycleLen(this))));

		ListPreference prefTheme = (ListPreference) findPreference(Settings.KEY_PREF_THEME);
		prefTheme.setSummary(prefTheme.getEntry());
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(Settings.KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN)) {
			Preference prefMcl = findPreference(Settings.KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN);
			prefMcl.setSummary(getString(
					R.string.pref_default_menstrual_cycle_len_summary,
					String.valueOf(Settings.getDefaultMenstrualCycleLen(this))));
		} else if (key.equals(Settings.KEY_PREF_THEME)) {
			ListPreference prefTheme = (ListPreference) findPreference(Settings.KEY_PREF_THEME);
			prefTheme.setSummary(prefTheme.getEntry());
			ThemeUtils.applyChanges(this);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
}
