package com.anna.sent.soft.womancyc.base;

import com.anna.sent.soft.activity.BaseFragment;
import com.anna.sent.soft.settings.SettingsLanguage;
import com.anna.sent.soft.settings.SettingsTheme;
import com.anna.sent.soft.womancyc.BuildConfig;
import com.anna.sent.soft.womancyc.shared.SettingsLanguageImpl;
import com.anna.sent.soft.womancyc.shared.SettingsThemeImpl;

public class WcFragment extends BaseFragment {
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
        return new SettingsLanguageImpl(getContext());
    }

    @Override
    protected SettingsTheme createSettingsTheme() {
        return new SettingsThemeImpl(getContext());
    }
}
