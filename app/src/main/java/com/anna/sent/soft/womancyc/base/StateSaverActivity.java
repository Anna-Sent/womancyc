package com.anna.sent.soft.womancyc.base;

import android.os.Bundle;
import android.util.Log;

import com.anna.sent.soft.strategy.statesaver.StateSaverBaseActivity;
import com.anna.sent.soft.utils.LanguageUtils;
import com.anna.sent.soft.utils.ThemeUtils;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Settings;
import com.anna.sent.soft.womancyc.utils.MyLog;

public abstract class StateSaverActivity extends StateSaverBaseActivity {
    private String wrapMsg(String msg) {
        return getClass().getSimpleName() + '@'
                + Integer.toHexString(hashCode()) + ": " + msg;
    }

    @Override
    public void log(String msg) {
        MyLog.getInstance().logcat(Log.DEBUG, wrapMsg(msg));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MyLog.getInstance().init(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setupTheme() {
        ThemeUtils.setupThemeBeforeOnActivityCreate(this,
                Settings.settingsTheme.getStyle(this, R.array.style,
                        R.style.AppTheme));
    }

    protected void setupLanguage() {
        LanguageUtils.setupLanguageAfterOnActivityCreate(this,
                Settings.settingsLanguage.isLanguageSetByUser(this),
                Settings.settingsLanguage.getLocale(this));
    }
}
