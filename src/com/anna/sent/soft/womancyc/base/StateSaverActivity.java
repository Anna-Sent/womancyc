package com.anna.sent.soft.womancyc.base;

import com.anna.sent.soft.strategy.statesaver.StateSaverBaseActivity;
import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public abstract class StateSaverActivity extends StateSaverBaseActivity {
	@Override
	protected void setupTheme() {
		ThemeUtils.onActivityCreateSetTheme(this);
	}

	protected void setupLanguage() {
	}
}