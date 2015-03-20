package com.anna.sent.soft.womancyc.base;

import com.anna.sent.soft.strategy.statesaver.StateSaverBaseActivity;
import com.anna.sent.soft.utils.LanguageUtils;
import com.anna.sent.soft.utils.ThemeUtils;
import com.anna.sent.soft.womancyc.R;
import com.anna.sent.soft.womancyc.shared.Settings;

public abstract class StateSaverActivity extends StateSaverBaseActivity {
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