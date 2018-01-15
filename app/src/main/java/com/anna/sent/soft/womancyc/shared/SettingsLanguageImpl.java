package com.anna.sent.soft.womancyc.shared;

import android.content.Context;
import android.content.SharedPreferences;

import com.anna.sent.soft.settings.SettingsLanguage;
import com.anna.sent.soft.womancyc.R;

public class SettingsLanguageImpl extends SettingsLanguage {
    private static final String KEY_PREF_LANGUAGE = "pref_language";
    private static final String KEY_PREF_IS_LANGUAGE_SET_BY_USER = "pref_is_language_set_by_user";

    public SettingsLanguageImpl(Context context) {
        super(context);
    }

    @Override
    protected SharedPreferences getSettings() {
        return Settings.getSettings(context);
    }

    @Override
    public String getLanguageKey() {
        return KEY_PREF_LANGUAGE;
    }

    @Override
    protected String getIsLanguageSetByUserKey() {
        return KEY_PREF_IS_LANGUAGE_SET_BY_USER;
    }

    @Override
    protected int getLanguages() {
        return R.array.language;
    }

    @Override
    protected int getLanguageIds() {
        return R.array.language_ids;
    }

    @Override
    protected int getLocales() {
        return R.array.locale;
    }

    @Override
    protected int getDefaultLanguageId() {
        return context.getResources().getInteger(R.integer.defaultLanguageId);
    }
}
