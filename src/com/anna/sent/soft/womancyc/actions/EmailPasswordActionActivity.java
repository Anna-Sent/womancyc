package com.anna.sent.soft.womancyc.actions;

import com.anna.sent.soft.utils.UserEmailFetcher;
import com.anna.sent.soft.womancyc.shared.Settings;

public class EmailPasswordActionActivity extends EmailActionActivity {
	@Override
	protected String getEmail() {
		return UserEmailFetcher.getEmail(this);
	}

	@Override
	protected String getText() {
		return Settings.getPassword(this);
	}
}