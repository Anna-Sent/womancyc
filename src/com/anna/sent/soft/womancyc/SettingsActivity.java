package com.anna.sent.soft.womancyc;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;

import com.anna.sent.soft.womancyc.data.Calculator;
import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {
	private static final String TAG = "moo";
	private static final boolean DEBUG = false;

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

		setupDefaultMenstrualCycleLenPreference();
		setupUseAvgPreference();
		setupPasswordPreference();
		setupLockAutomaticallyPreference();
		setupThemePreference();
	}

	private void setupDefaultMenstrualCycleLenPreference() {
		Preference pref = findPreference(Settings.KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN);
		pref.setSummary(getString(
				R.string.pref_default_menstrual_cycle_len_summary,
				String.valueOf(Settings.getDefaultMenstrualCycleLen(this)),
				getString(R.string.pref_use_average_title)));
	}

	private void setupUseAvgPreference() {
		Preference pref = findPreference(Settings.KEY_PREF_USE_AVG);
		pref.setSummary(getString(R.string.pref_use_average_summary,
				String.valueOf(Calculator.getMaxMenstrualCycleLen(this))));
	}

	private void setupPasswordPreference() {
		Preference pref = findPreference(Settings.KEY_PREF_PASSWORD);
		boolean isPasswordSet = Settings.isPasswordSet(this);
		pref.setSummary(isPasswordSet ? getString(R.string.isSet)
				: getString(R.string.isNotSet));
		Preference pref1 = findPreference(Settings.KEY_PREF_LOCK_AUTOMATICALLY);
		pref1.setEnabled(isPasswordSet);
		Preference pref2 = findPreference(Settings.KEY_PREF_HIDE_WIDGET);
		pref2.setEnabled(isPasswordSet);
	}

	private void setupLockAutomaticallyPreference() {
		Preference pref = findPreference(Settings.KEY_PREF_LOCK_AUTOMATICALLY);
		if (Settings.lockAutomatically(this)) {
			pref.setSummary(R.string.pref_lock_automatically_summary_on);
		} else {
			pref.setSummary(getString(
					R.string.pref_lock_automatically_summary_off,
					getString(R.string.lockAndExit)));
		}
	}

	private void setupThemePreference() {
		ListPreference pref = (ListPreference) findPreference(Settings.KEY_PREF_THEME);
		pref.setSummary(pref.getEntry());
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals(Settings.KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN)) {
			setupDefaultMenstrualCycleLenPreference();
		} else if (key.equals(Settings.KEY_PREF_PASSWORD)) {
			setupPasswordPreference();
		} else if (key.equals(Settings.KEY_PREF_THEME)) {
			setupThemePreference();
			ThemeUtils.applyChanges(this);
		} else if (key.equals(Settings.KEY_PREF_LOCK_AUTOMATICALLY)) {
			setupLockAutomaticallyPreference();
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
