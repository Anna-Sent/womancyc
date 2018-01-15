package com.anna.sent.soft.womancyc.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.anna.sent.soft.settings.SettingsTheme;
import com.anna.sent.soft.womancyc.R;

public class SettingsThemeImpl extends SettingsTheme {
    private static final String KEY_PREF_THEME = "pref_theme";

    public SettingsThemeImpl(Context context) {
        super(context);
    }

    @Override
    protected SharedPreferences getSettings() {
        return Settings.getSettings(context);
    }

    @Override
    public String getThemeKey() {
        return KEY_PREF_THEME;
    }

    @Override
    protected int getThemes() {
        return R.array.theme;
    }

    @Override
    protected int getThemeIds() {
        return R.array.theme_ids;
    }

    @Override
    protected int getStyles() {
        return R.array.style;
    }

    @Override
    protected int getDefaultThemeId() {
        return context.getResources().getInteger(R.integer.defaultThemeId);
    }
}
