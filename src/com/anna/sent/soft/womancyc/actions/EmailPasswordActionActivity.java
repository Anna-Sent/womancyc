package com.anna.sent.soft.womancyc.actions;

import com.anna.sent.soft.womancyc.utils.UserEmailFetcher;

public class EmailPasswordActionActivity extends EmailActionActivity {
	@Override
	protected String getEmail() {
		return UserEmailFetcher.getEmail(this);
	}

	@Override
	protected String getText() {
		return null;
	}
}
