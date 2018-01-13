package com.anna.sent.soft.womancyc;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.util.Log;
import android.view.MenuItem;

import com.anna.sent.soft.utils.ActionBarUtils;
import com.anna.sent.soft.utils.ActivityUtils;
import com.anna.sent.soft.utils.LanguageUtils;
import com.anna.sent.soft.utils.NavigationUtils;
import com.anna.sent.soft.utils.ThemeUtils;
import com.anna.sent.soft.womancyc.data.Calculator;
import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.utils.MyLog;

public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {
    private final static String KEY_PREF_UI_SETTINGS = "pref_ui_settings";
    private final static String KEY_PREF_SEND_PASSWORD_TO_EMAIL = "pref_send_password_to_email";

    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }

    private void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ThemeUtils.setupThemeBeforeOnActivityCreate(this,
                Settings.settingsTheme.getStyle(this, R.array.style, R.style.AppTheme));

        super.onCreate(savedInstanceState);

        LanguageUtils.setupLanguageAfterOnActivityCreate(this,
                Settings.settingsLanguage.isLanguageSetByUser(this),
                Settings.settingsLanguage.getLocale(this));

        setTitle(R.string.settings);

        addPreferencesFromResource(R.xml.preferences);

        ActionBarUtils.setupActionBar(this);

        createLanguagePreference();
        setupThemePreference();

        setupDefaultMenstrualCycleLenPreference();
        setupUseAvgPreference();
        setupPasswordPreference();
        setupLockAutomaticallyPreference();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavigationUtils.navigateUp(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createLanguagePreference() {
        log("create language preference");
        PreferenceCategory category = (PreferenceCategory) findPreference(KEY_PREF_UI_SETTINGS);
        final ListPreference pref = new ListPreference(this);
        pref.setKey(Settings.settingsLanguage.getLanguageKey(this));
        String[] entries = getResources().getStringArray(R.array.language);
        pref.setEntries(entries);
        String[] entryValues = getResources().getStringArray(
                R.array.language_values);
        pref.setEntryValues(entryValues);
        String title = getString(R.string.pref_language_title);
        pref.setDialogTitle(title);
        pref.setTitle(title);
        int value = Settings.settingsLanguage.getLanguage(this);
        pref.setDefaultValue(String.valueOf(value));
        pref.setValue(String.valueOf(value));
        Settings.settingsLanguage.setLanguage(this, value);
        log(pref.getValue() + " " + pref.getEntry());
        pref.setSummary(pref.getEntry());
        category.addPreference(pref);
    }

    private void setupThemePreference() {
        ListPreference pref = (ListPreference) findPreference(Settings.settingsTheme
                .getThemeKey(this));
        pref.setSummary(pref.getEntry());
    }

    private void setupDefaultMenstrualCycleLenPreference() {
        Preference pref = findPreference(Settings.KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN);
        String mcl = String.valueOf(Settings.getDefaultMenstrualCycleLen(this));
        if (Settings.useAverage(this)) {
            pref.setSummary(getString(R.string.pref_default_menstrual_cycle_len_summary_on, mcl));
        } else {
            pref.setSummary(getString(R.string.pref_default_menstrual_cycle_len_summary_off, mcl));
        }
    }

    private void setupUseAvgPreference() {
        Preference pref = findPreference(Settings.KEY_PREF_USE_AVG);
        pref.setSummary(getString(R.string.pref_use_average_summary,
                String.valueOf(Calculator.getMaxMenstrualCycleLen(this))));
    }

    private void setupPasswordPreference() {
        Preference pref = findPreference(Settings.KEY_PREF_PASSWORD);
        boolean isPasswordSet = Settings.isPasswordSet(this);
        pref.setSummary(isPasswordSet ? getString(R.string.isSet) : getString(R.string.isNotSet));

        String[] keys = new String[]{KEY_PREF_SEND_PASSWORD_TO_EMAIL,
                Settings.KEY_PREF_LOCK_AUTOMATICALLY,
                Settings.KEY_PREF_HIDE_WIDGET};
        for (String key : keys) {
            Preference pref_i = findPreference(key);
            pref_i.setEnabled(isPasswordSet);
        }
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(Settings.KEY_PREF_DEFAULT_MENSTRUAL_CYCLE_LEN)) {
            setupDefaultMenstrualCycleLenPreference();
        } else if (key.equals(Settings.KEY_PREF_USE_AVG)) {
            setupDefaultMenstrualCycleLenPreference();
        } else if (key.equals(Settings.KEY_PREF_PASSWORD)) {
            setupPasswordPreference();
        } else if (key.equals(Settings.KEY_PREF_LOCK_AUTOMATICALLY)) {
            setupLockAutomaticallyPreference();
        } else if (key.equals(Settings.settingsLanguage.getLanguageKey(this))) {
            ActivityUtils.restartActivity(this);
        } else if (key.equals(Settings.settingsTheme.getThemeKey(this))) {
            ActivityUtils.restartActivity(this);
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
