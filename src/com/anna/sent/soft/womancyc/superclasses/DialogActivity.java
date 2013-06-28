package com.anna.sent.soft.womancyc.superclasses;

import com.anna.sent.soft.womancyc.utils.ThemeUtils;

public abstract class DialogActivity extends DataKeeperActivity {
	@Override
	protected void setupTheme() {
		ThemeUtils.onDialogStyleActivityCreateSetTheme(this);
	}
}
