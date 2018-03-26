package com.anna.sent.soft.womancyc.base;

import android.annotation.SuppressLint;

import com.anna.sent.soft.activity.BaseActivity;
import com.anna.sent.soft.settings.SettingsLanguage;
import com.anna.sent.soft.settings.SettingsTheme;
import com.anna.sent.soft.womancyc.BuildConfig;
import com.anna.sent.soft.womancyc.shared.SettingsLanguageImpl;
import com.anna.sent.soft.womancyc.shared.SettingsThemeImpl;

@SuppressLint("Registered")
public class WcActivity extends BaseActivity {
    @Override
    protected String getAppTag() {
        return WcConstants.TAG;
    }

    @Override
    protected boolean enableCrashReporting() {
        return BuildConfig.ENABLE_CRASHLYTICS;
    }

    @Override
    protected SettingsLanguage createSettingsLanguage() {
        return new SettingsLanguageImpl(this);
    }

    @Override
    protected SettingsTheme createSettingsTheme() {
        return new SettingsThemeImpl(this);
    }
}
